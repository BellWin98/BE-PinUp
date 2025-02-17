package com.pinup.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(500, "G001", "내부 서버 오류입니다."),
    METHOD_NOT_ALLOWED(405, "G002", "허용되지 않은 HTTP method입니다."),
    INPUT_VALUE_INVALID(400, "G003", "유효하지 않은 입력입니다."),
    INPUT_TYPE_INVALID(400, "G004", "입력 타입이 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),
    HTTP_HEADER_INVALID(400, "G006", "request header가 유효하지 않습니다."),
    FILE_EXTENSION_INVALID(400, "G007", "지원하지 않는 파일 포맷입니다."),
    FILE_CONVERT_FAIL(500, "G008", "변환할 수 없는 파일입니다."),
    ENTITY_TYPE_INVALID(500, "G009", "올바르지 않은 entity type 입니다."),
    FILTER_MUST_RESPOND(500, "G010", "필터에서 처리해야 할 요청이 Controller에 접근하였습니다."),
    FILE_DELETE_ERROR(500, "G011", "파일 삭제 중 오류가 발생했습니다."),
    CACHE_SERIALIZATION_ERROR(500, "G012", "캐시 데이터 직렬화 중 오류가 발생했습니다."),
    CACHE_DESERIALIZATION_ERROR(500, "G013", "캐시 데이터 역직렬화 중 오류가 발생했습니다."),
    CACHE_OPERATION_ERROR(500, "G014", "캐시 작업 중 오류가 발생했습니다."),
    VALIDATION_FAILED(400, "G015", "입력값 유효성 검사에 실패하였습니다."),
    IMAGES_LIMIT_EXCEEDED(400, "G016", "등록 가능한 이미지 갯수를 초과했습니다."),
    INVALID_FILE_URL(400, "G17", "잘못된 파일 URL 형식입니다."),
    CACHE_KEY_NULL(400, "G018", "캐시 키는 null일 수 없습니다."),
    SOCIAL_LOGIN_TOKEN_NOT_FOUND(500, "G019", "소셜 로그인 서버로부터 발급된 Access Token이 없습니다."),
    SOCIAL_LOGIN_USER_INFO_NOT_FOUND(500, "G020", "소셜 로그인 서버에서 조회한 유저 정보가 없습니다."),
    FILE_UPLOAD_ERROR(400, "G021", "파일 업로드를 실패했습니다."),

    // Auth
    INVALID_TOKEN(400, "AU001", "유효하지 않은 토큰입니다."),
    NOT_EXPIRED_ACCESS_TOKEN(400, "AU002", "만료되지 않은 Access Token입니다."),
    FORBIDDEN(403, "AU003", "접근할 수 있는 권한이 없습니다."),
    EXPIRED_OR_PREVIOUS_REFRESH_TOKEN(403, "AU004", "만료되었거나 이전에 발급된 Refresh Token입니다."),
    ACCESS_DENIED(401, "AU005", "유효한 인증 정보가 아닙니다."),
    EXPIRED_ACCESS_TOKEN(401, "AU006", "Access Token이 만료되었습니다. 토큰을 재발급해주세요"),
    LOGIN_TYPE_NOT_FOUND(400, "AU007", "일치하는 로그인 타입을 찾을 수 없습니다."),

    // Member
    MEMBER_NOT_FOUND(404, "M001", "존재하지 않는 유저입니다."),
    ALREADY_EXIST_NICKNAME(400, "M002", "중복된 닉네임입니다."),
    ALREADY_EXIST_EMAIL(400, "M003", "이미 가입된 이메일입니다."),
    PASSWORD_MISMATCH(400, "M004", "비밀번호가 일치하지 않습니다."),
    NICKNAME_UPDATE_TIME_LIMIT(400, "M005", "닉네임은 30일에 한 번만 변경할 수 있습니다."),

    // Review
    REVIEW_NOT_FOUND(404, "R001", "존재하지 않는 리뷰입니다."),

    // Place
    PLACE_NOT_FOUND(404, "P001", "존재하지 않는 장소입니다."),
    PLACE_CATEGORY_NOT_FOUND(404, "P002", "존재하지 않는 장소 카테고리입니다."),
    PLACE_SORT_NOT_FOUND(404, "P003", "존재하지 않는 장소 정렬 필터입니다."),

    // Friend
    ALREADY_EXIST_FRIEND_REQUEST(400, "F001", "이미 존재하는 친구 요청입니다."),
    SELF_FRIEND_REQUEST(400, "F002", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    ALREADY_PROCESSED_FRIEND_REQUEST(400, "F003", "이미 처리된 친구 요청입니다."),
    FRIEND_REQUEST_NOT_FOUND(404, "F004", "존재하지 않는 친구 요청입니다."),
    FRIENDSHIP_NOT_FOUND(404, "F005", "존재하지 않는 친구 관계입니다."),
    FRIEND_NOT_FOUND(404, "F006", "해당 이름을 가진 친구를 찾을 수 없습니다."),
    ALREADY_FRIEND(400, "F007", "이미 친구 관계입니다."),
    FRIEND_REQUEST_RECEIVER_MISMATCH(403, "F008", "현재 사용자가 친구 요청의 수신자가 아닙니다."),

    // Alarm
    SSE_CONNECTION_ERROR(500, "AL001", "SSE 연결 중 오류가 발생했습니다."),
    ALARM_NOT_FOUND(404, "AL002", "존재하지 않는 알람입니다."),
    UNAUTHORIZED_ALARM_ACCESS(403, "AL003", "해당 알람에 접근할 권한이 없습니다."),

    // Article
    ARTICLE_NOT_FOUND(404, "AR001", "존재하지 않는 아티클입니다."),

    // BookMark
    BOOKMARK_NOT_FOUND(404, "B001", "존재하지 않는 북마크입니다."),
    ALREADY_EXIST_BOOKMARK(400, "B002", "이미 존재하는 북마크입니다."),
    UNAUTHORIZED_BOOKMARK_ACCESS(403, "B003", "해당 북마크에 접근할 권한이 없습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
