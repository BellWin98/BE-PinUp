package com.pinup.domain.member.service;

import com.pinup.domain.member.dto.request.LoginRequest;
import com.pinup.domain.member.dto.request.SignUpRequest;
import com.pinup.domain.member.dto.response.LoginResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.exception.ExpiredTokenException;
import com.pinup.domain.member.exception.InvalidTokenException;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.common.image.entity.Image;
import com.pinup.global.config.jwt.JwtTokenProvider;
import com.pinup.global.config.redis.RedisService;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {
    private static final String PROFILE_IMAGE_DIRECTORY = "profiles";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    private final ProfileImageService profileImageService;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${oauth2.google.token-uri}")
    private String googleTokenUri;

    @Value("${oauth2.google.resource-uri}")
    private String googleResourceUri;

/*    @Value("${oauth2.google.auth-uri}")
    private String googleAuthUri;*/

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        Member createdMember = signUpRequest.toEntity();
        String profileImageUrl = signUpRequest.getProfileImageUrl();
        profileImageService.saveProfileImage(createdMember, profileImageUrl);
        Member savedMember = memberRepository.save(createdMember);
        eventPublisher.publishEvent(savedMember);
    }

    @Transactional
    public LoginResponse socialLogin(final LoginRequest loginRequest) {
        Member findMember = memberRepository.findBySocialId(loginRequest.getSocialId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        String accessToken = jwtTokenProvider.createAccessToken(findMember.getSocialId(), findMember.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(findMember.getSocialId());
        redisService.setValues(REFRESH_TOKEN_PREFIX+findMember.getSocialId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken, MemberResponse.from(findMember));
    }

    @Transactional
    public LoginResponse googleLogin(String code) {
        String accessToken = getAccessToken(code);
        Map<String, Object> userInfo = getUserInfo(accessToken);
        String socialId = (String) userInfo.get("sub");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String profilePictureUrl = (String) userInfo.get("picture");
        Member member = memberRepository.findBySocialId(socialId).orElse(null);
        if (member == null) {
            member = memberRepository.save(Member.builder()
                    .email(email)
                    .name(name)
                    .loginType(LoginType.GOOGLE)
                    .socialId(socialId)
                    .build());
            member.updateProfileImage(new Image(new S3Service.S3FileInfo("", profilePictureUrl, "")));
            eventPublisher.publishEvent(member);
        }
        String jwtToken = jwtTokenProvider.createAccessToken(member.getSocialId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(socialId);
        redisService.setValues(REFRESH_TOKEN_PREFIX+member.getSocialId(), refreshToken);

        return new LoginResponse(jwtToken, refreshToken, MemberResponse.from(member));
    }

    public LoginResponse refresh(String refreshToken) {
        if (isTokenValidate(refreshToken)) {
            String socialId = getSocialIdByToken(refreshToken);
            Member member = getMemberBySocialId(socialId);
            String storedRefreshToken = redisService.getValues(REFRESH_TOKEN_PREFIX+member.getSocialId());
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new InvalidTokenException();
            }
            String newAccessToken = jwtTokenProvider.createAccessToken(socialId, member.getRole());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(socialId);
            redisService.setValues(REFRESH_TOKEN_PREFIX+member.getSocialId(), newRefreshToken);

            return new LoginResponse(newAccessToken, newRefreshToken, MemberResponse.from(member));
        }

        return null;
    }

    public void logout(String accessToken) {
        if (isTokenValidate(accessToken)) {
            String socialId = getSocialIdByToken(accessToken);
            Member member = getMemberBySocialId(socialId);
            redisService.deleteValues(REFRESH_TOKEN_PREFIX+member.getSocialId());
        }
    }

    private String getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(googleTokenUri, HttpMethod.POST, request, Map.class);
        if (response.getBody() == null) {
            throw new EntityNotFoundException(ErrorCode.SOCIAL_LOGIN_TOKEN_NOT_FOUND);
        }

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<Map> response = restTemplate.exchange(googleResourceUri, HttpMethod.GET, entity, Map.class);

        if (response.getBody() == null) {
            throw new EntityNotFoundException(ErrorCode.SOCIAL_LOGIN_USER_INFO_NOT_FOUND);
        }

        return response.getBody();
    }

    private Member getMemberBySocialId(String socialId) {
        return memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private boolean isTokenValidate(String token) {
        try {
            return jwtTokenProvider.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    private String getSocialIdByToken(String token) {
        return jwtTokenProvider.getSocialId(token);
    }

    /* 미사용 코드
    public String getGoogleAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl(googleAuthUri)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid")
                .toUriString();
    }

    @Transactional
    public LoginResponse normalLogin(NormalLoginRequest request) {
        String email = request.getEmail();
        Member member = getMemberByEmail(email);
        validatePassword(request.getPassword(), member.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        redisService.setValues(REFRESH_TOKEN_PREFIX+member.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken, MemberResponse.from(member));
    }

    @Transactional
    public void join(MemberJoinRequest request) {
        validateExistEmail(request.getEmail());
        Member newMember = Member.builder()
                .email(request.getEmail())
//                .nickname(request.getNickname())
                .name(request.getName())
                .loginType(LoginType.NORMAL)
                .profileImageUrl(request.getProfileImageUrl())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        memberRepository.save(newMember);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validatePassword(String requestPassword, String memberPassword) {
        if (!passwordEncoder.matches(requestPassword, memberPassword)) {
            throw new PasswordMismatchException();
        }
    }

    private void validateExistEmail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new EntityAlreadyExistException(ErrorCode.ALREADY_EXIST_EMAIL);
                });
    }

    @Transactional
    public LoginResponse getTokens(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, Object> userInfo = getUserInfo(token);
        Member createdMember = createMember(userInfo);
        String accessToken = jwtTokenProvider.createAccessToken(createdMember.getEmail(), createdMember.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(createdMember.getEmail());
        redisService.setValues(REFRESH_TOKEN_PREFIX+createdMember.getSocialId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken, MemberResponse.from(createdMember));
    }

    private Member createMember(Map<String, Object> userInfo) {
        String socialId = (String) userInfo.get("sub");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String profilePictureUrl = (String) userInfo.get("picture");

        return memberRepository.findBySocialId(socialId)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(email)
                        .name(name)
                        .profileImageUrl(profilePictureUrl)
                        .loginType(LoginType.GOOGLE)
                        .socialId(socialId)
                        .build()));
    }
     */
}