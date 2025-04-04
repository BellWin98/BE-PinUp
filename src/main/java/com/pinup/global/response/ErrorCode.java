package com.pinup.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(500, "E_GLOBAL001", "내부 서버 오류입니다."),
    METHOD_NOT_ALLOWED(405, "E_GLOBAL002", "허용되지 않은 HTTP method입니다."),
    INPUT_VALUE_INVALID(400, "E_GLOBAL003", "유효하지 않은 입력입니다."),
    INPUT_TYPE_INVALID(400, "E_GLOBAL004", "입력 타입이 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "E_GLOBAL005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),
    HTTP_HEADER_INVALID(400, "E_GLOBAL006", "request header가 유효하지 않습니다."),
    ENTITY_TYPE_INVALID(500, "E_GLOBAL007", "올바르지 않은 entity type 입니다."),
    FILTER_MUST_RESPOND(500, "E_GLOBAL008", "필터에서 처리해야 할 요청이 Controller에 접근하였습니다."),
    CACHE_SERIALIZATION_ERROR(500, "E_GLOBAL009", "캐시 데이터 직렬화 중 오류가 발생했습니다."),
    CACHE_DESERIALIZATION_ERROR(500, "E_GLOBAL010", "캐시 데이터 역직렬화 중 오류가 발생했습니다."),
    CACHE_OPERATION_ERROR(500, "E_GLOBAL011", "캐시 작업 중 오류가 발생했습니다."),
    VALIDATION_FAILED(400, "E_GLOBAL012", "입력값 유효성 검사에 실패하였습니다."),
    CACHE_KEY_NULL(400, "E_GLOBAL013", "캐시 키는 null일 수 없습니다."),
    NO_RESOURCE_FOUND(404, "E_GLOBAL014", "해당 경로의 URI를 찾을 수 없습니다."),

    // File
    FILE_EXTENSION_INVALID(400, "E_FILE001", "지원하지 않는 파일 포맷입니다."),
    FILE_UPLOAD_ERROR(400, "E_FILE002", "파일 업로드를 실패했습니다."),
    IMAGE_IS_EMPTY(400, "E_FILE003", "이미지 파일이 비어있습니다."),
    IMAGE_SIZE_IS_TOO_BIG(400, "E_FILE004", "이미지 크기가 너무 큽니다. 최대 허용 크기: 10MB"),
    IMAGE_PROCESSING_ERROR(400, "E_FILE005", "이미지 처리 중 오류가 발생했습니다."),
    FILE_CONVERT_FAIL(500, "E_FILE006", "변환할 수 없는 파일입니다."),
    FILE_DELETE_ERROR(500, "E_FILE007", "파일 삭제 중 오류가 발생했습니다."),
    IMAGES_LIMIT_EXCEEDED(400, "E_FILE008", "등록 가능한 이미지 갯수를 초과했습니다."),
    INVALID_FILE_URL(400, "E_FILE009", "잘못된 파일 URL 형식입니다."),
    IMAGE_PIXEL_IS_TOO_BIG(400, "E_FILE010", "이미지 해상도가 너무 큽니다."),

    // Auth
    INVALID_TOKEN(400, "E_AUTH001", "유효하지 않은 토큰입니다."),
    NOT_EXPIRED_ACCESS_TOKEN(400, "E_AUTH002", "만료되지 않은 Access Token입니다."),
    FORBIDDEN(403, "E_AUTH003", "접근할 수 있는 권한이 없습니다."),
    EXPIRED_OR_PREVIOUS_REFRESH_TOKEN(403, "E_AUTH004", "만료되었거나 이전에 발급된 Refresh Token입니다."),
    ACCESS_DENIED(401, "E_AUTH005", "유효한 인증 정보가 아닙니다."),
    EXPIRED_TOKEN(401, "E_AUTH006", "토큰 유효기간이 만료되었습니다."),
    LOGIN_TYPE_NOT_FOUND(400, "E_AUTH007", "일치하는 로그인 타입을 찾을 수 없습니다."),
    SOCIAL_LOGIN_TOKEN_NOT_FOUND(500, "E_AUTH008", "소셜 로그인 서버로부터 발급된 Access Token이 없습니다."),
    SOCIAL_LOGIN_USER_INFO_NOT_FOUND(500, "E_AUTH009", "소셜 로그인 서버에서 조회한 유저 정보가 없습니다."),

    // Member
    MEMBER_NOT_FOUND(400, "E_MEMBER001", "존재하지 않는 유저입니다."),
    ALREADY_EXIST_NICKNAME(400, "E_MEMBER002", "중복된 닉네임입니다."),
    ALREADY_EXIST_EMAIL(400, "E_MEMBER003", "이미 가입된 이메일입니다."),
    PASSWORD_MISMATCH(400, "E_MEMBER004", "비밀번호가 일치하지 않습니다."),
    NICKNAME_UPDATE_TIME_LIMIT(400, "E_MEMBER005", "닉네임은 30일에 한 번만 변경할 수 있습니다."),

    // Review
    REVIEW_NOT_FOUND(400, "E_REVIEW001", "존재하지 않는 리뷰입니다."),
    UNAUTHORIZED_REVIEW_ACCESS(403, "E_REVIEW002", "해당 리뷰에 접근할 권한이 없습니다."),
    
    // Place
    PLACE_NOT_FOUND(400, "E_PLACE001", "존재하지 않는 장소입니다."),
    PLACE_CATEGORY_NOT_FOUND(400, "E_PLACE002", "존재하지 않는 장소 카테고리입니다."),
    PLACE_SORT_NOT_FOUND(400, "E_PLACE003", "존재하지 않는 장소 정렬 필터입니다."),

    // Friend
    ALREADY_EXIST_FRIEND_REQUEST_BY_MEMBER(400, "E_FRIEND001", "이미 친구 요청을 보냈습니다."),
    SELF_FRIEND_REQUEST(400, "E_FRIEND002", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    ALREADY_PROCESSED_FRIEND_REQUEST(400, "E_FRIEND003", "이미 처리된 친구 요청입니다."),
    FRIEND_REQUEST_NOT_FOUND(400, "E_FRIEND004", "존재하지 않는 친구 요청입니다."),
    FRIENDSHIP_NOT_FOUND(400, "E_FRIEND005", "존재하지 않는 친구 관계입니다."),
    FRIEND_NOT_FOUND(400, "E_FRIEND006", "해당 이름을 가진 친구를 찾을 수 없습니다."),
    ALREADY_FRIEND(400, "E_FRIEND007", "이미 친구 관계입니다."),
    FRIEND_REQUEST_RECEIVER_MISMATCH(403, "E_FRIEND008", "권한이 없습니다."),
    FRIEND_REQUEST_SENDER_MISMATCH(403, "E_FRIEND009", "현재 사용자가 친구 요청의 발신자가 아닙니다."),
    ALREADY_EXIST_FRIEND_REQUEST_BY_FRIEND(400, "E_FRIEND010", "상대방이 이미 친구 요청을 보냈습니다. 수락해주세요."),

    // Alarm
    SSE_CONNECTION_ERROR(500, "E_ALARM001", "SSE 연결 중 오류가 발생했습니다."),
    ALARM_NOT_FOUND(400, "E_ALARM002", "존재하지 않는 알람입니다."),
    UNAUTHORIZED_ALARM_ACCESS(403, "E_ALARM003", "해당 알람에 접근할 권한이 없습니다."),

    // Article
    ARTICLE_NOT_FOUND(400, "E_ARTICLE001", "존재하지 않는 아티클입니다."),

    // BookMark
    BOOKMARK_NOT_FOUND(400, "E_BOOKMARK001", "존재하지 않는 북마크입니다."),
    ALREADY_EXIST_BOOKMARK(400, "E_BOOKMARK002", "이미 존재하는 북마크입니다."),
    UNAUTHORIZED_BOOKMARK_ACCESS(403, "E_BOOKMARK003", "해당 북마크에 접근할 권한이 없습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
