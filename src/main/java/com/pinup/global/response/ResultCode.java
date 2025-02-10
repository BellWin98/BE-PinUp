package com.pinup.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCode {

    // Auth
    SOCIAL_LOGIN_SUCCESS(200, "AU001", "소셜 로그인에 성공하였습니다."),
    TOKEN_ISSUED_SUCCESS(200, "AU002", "토큰 발급에 성공하였습니다."),
    TOKEN_REISSUED_SUCCESS(200, "AU003", "토큰 재발급에 성공하였습니다."),
    LOGOUT_SUCCESS(200, "AU004", "로그아웃에 성공하였습니다."),
    NORMAL_LOGIN_SUCCESS(200, "AU005", "일반 로그인에 성공하였습니다."),
    SIGN_UP_SUCCESS(200, "AU006", "회원가입에 성공하였습니다."),

    // Member
    GET_MEMBERS_SUCCESS(200, "M001", "유저 목록 조회에 성공하였습니다."),
    GET_LOGIN_USER_INFO_SUCCESS(200, "M002", "현재 로그인한 유저 정보 조회에 성공하였습니다."),
    GET_USER_INFO_SUCCESS(200, "M003", "유저 정보 조회에 성공하였습니다."),
    DELETE_USER_SUCCESS(200, "M004", "유저 삭제에 성공하였습니다."),
    GET_NICKNAME_DUPLICATE_SUCCESS(200, "M005", "닉네임 중복 여부 확인에 성공하였습니다."),
    UPDATE_MEMBER_INFO_SUCCESS(200, "M006", "유저 정보 수정에 성공하였습니다."),
    GET_MY_FEED_SUCCESS(200, "M007", "내 피드 조회에 성공하였습니다."),
    GET_MEMBER_FEED_SUCCESS(200, "M008", "유저 피드 조회에 성공하였습니다."),

    // Place
    GET_PLACES_SUCCESS(200, "P001", "장소 목록 조회에 성공하였습니다."),
    GET_PLACE_DETAIL_SUCCESS(200, "P002", "장소 상세 조회에 성공하였습니다."),

    // Review
    CREATE_REVIEW_SUCCESS(201, "R001", "리뷰 등록에 성공하였습니다."),
    GET_REVIEW_DETAIL_SUCCESS(200, "R002", "리뷰 상세 조회에 성공하였습니다."),
    GET_MY_PHOTO_REVIEW_PREVIEW_SUCCESS(200, "R003", "내 포토 리뷰 미리보기 목록 조회에 성공하였습니다."),
    GET_MY_TEXT_REVIEW_SUCCESS(200, "R004", "내 텍스트 리뷰 목록 조회에 성공하였습니다."),
    GET_MEMBER_PHOTO_REVIEW_SUCCESS(200, "R005", "멤버의 포토 리뷰 목록 조회에 성공하였습니다."),
    GET_MEMBER_PHOTO_REVIEW_PREVIEW_SUCCESS(200, "R006", "멤버의 포토 리뷰 미리보기 목록 조회에 성공하였습니다."),
    GET_MEMBER_TEXT_REVIEW_SUCCESS(200, "R007", "멤버의 텍스트 리뷰 목록 조회에 성공하였습니다."),

    // Friend
    REQUEST_PIN_BUDDY_SUCCESS(201, "F001", "핀버디 신청이 완료되었습니다."),
    ACCEPT_PIN_BUDDY_SUCCESS(200, "F002", "핀버디 신청을 수락했습니다."),
    REJECT_PIN_BUDDY_SUCCESS(200, "F003", "핀버디 신청을 거절했습니다."),
    CANCEL_PIN_BUDDY_SUCCESS(200, "F004", "핀버디 신청을 취소했습니다."),
    REMOVE_PIN_BUDDY_SUCCESS(200, "F005", "핀버디를 삭제하였습니다."),
    GET_RECEIVED_PIN_BUDDY_REQUEST_LIST_SUCCESS(200, "F006", "받은 핀버디 신청 목록을 조회하였습니다."),
    GET_MY_PIN_BUDDY_LIST_SUCCESS(200, "F007", "나의 핀버디 목록을 조회하였습니다."),
    GET_USER_PIN_BUDDY_LIST_SUCCESS(200, "F008", "해당 유저의 핀버디 목록을 조회하였습니다."),
    GET_MY_PIN_BUDDY_INFO_SUCCESS(200, "F009", "나의 핀버디 정보를 조회하였습니다."),

    // Alarm
    GET_ALARMS_SUCCESS(200, "AL001", "알림 목록을 조회하였습니다."),
    GET_ALARM_DETAIL_SUCCESS(200, "AL002", "알림 상세 내용을 조회하였습니다."),

    // Article
    CREATE_ARTICLE_SUCCESS(201, "AR001", "에디터 아티클을 생성하였습니다."),
    GET_ARTICLE_DETAIL_SUCCESS(200, "AR002", "에디터 아티클을 조회하였습니다."),
    GET_ARTICLE_LIST_SUCCESS(200, "AR003", "에디터 아티클 목록을 조회하였습니다."),

    //BookMark
    CREATE_BOOKMARK_SUCCESS(201, "B001", "북마크 등록에 성공하였습니다."),
    GET_MY_BOOKMARK_SUCCESS(200, "B002", "내 북마크 목록 조회에 성공하였습니다."),
    DELETE_BOOKMARK_SUCCESS(200, "B003", "북마크 삭제에 성공하였습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
