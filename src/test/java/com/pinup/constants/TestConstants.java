package com.pinup.constants;

import com.pinup.domain.member.entity.LoginType;

import java.util.List;

public class TestConstants {

    /* Member */
    public static final String TEST_MEMBER_EMAIL = "test@example.com";
    public static final String TEST_MEMBER_NAME = "테스트 사용자";
    public static final String TEST_MEMBER_NICKNAME = "테스트닉네임";
    public static final String TEST_MEMBER_IMAGE_URL = "test image url";

    public static final String SECOND_TEST_MEMBER_EMAIL = "test2@example.com";
    public static final String SECOND_TEST_MEMBER_NAME = "테스트 사용자2";
    public static final String SECOND_TEST_MEMBER_NICKNAME = "테스트 닉네임2";
    public static final String SECOND_TEST_MEMBER_IMAGE_URL = "test image url2";
    public static final LoginType TEST_MEMBER_LOGIN_TYPE = LoginType.GOOGLE;
    public static final String TEST_MEMBER_SOCIAL_ID = "test_social_id";

    /* Auth */
    public static final String TEST_ACCESS_TOKEN = "test_access_token";
    public static final String TEST_REFRESH_TOKEN = "test_refresh_token";
    public static final String TEST_NEW_ACCESS_TOKEN = "new_test_access_token";
    public static final String TEST_NEW_REFRESH_TOKEN = "new_test_refresh_token";
    public static final String TEST_GOOGLE_CODE = "test_google_code";
    public static final String TEST_PROFILE_IMAGE = "http://example.com/profile.jpg";
    public static final String TEST_INVALID_TOKEN = "invalid_token";

    /* Review */
    public static final String TEST_REVIEW_COMMENT = "너무 맛있어요!";
    public static final Double TEST_REVIEW_RATING = 3.5;
    public static final List<String> TEST_REVIEW_KEYWORDS = List.of("맛집","데이트","친구");

    /* Place */
    public static final String TEST_PLACE_KAKAO_PLACE_ID = "24484207";
    public static final String TEST_PLACE_NAME = "설빙 노원역점";
    public static final String TEST_PLACE_CATEGORY = "CAFE";
    public static final String TEST_PLACE_ADDRESS = "서울 노원구 상계동 602-3";
    public static final String TEST_PLACE_ROAD_ADDRESS = "서울 노원구 상계로 65";
    public static final String TEST_PLACE_LONGITUDE = "127.06234643609528";
    public static final String TEST_PLACE_LATITUDE = "37.6562967911604";
}