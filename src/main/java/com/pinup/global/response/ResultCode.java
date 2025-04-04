package com.pinup.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCode {

    // Auth
    SOCIAL_LOGIN_SUCCESS(200, "S_AUTH001", "소셜 로그인에 성공하였습니다."),
    TOKEN_ISSUED_SUCCESS(200, "S_AUTH002", "토큰 발급에 성공하였습니다."),
    TOKEN_REISSUED_SUCCESS(200, "S_AUTH003", "토큰 재발급에 성공하였습니다."),
    LOGOUT_SUCCESS(200, "S_AUTH004", "로그아웃에 성공하였습니다."),
    NORMAL_LOGIN_SUCCESS(200, "S_AUTH005", "일반 로그인에 성공하였습니다."),
    SIGN_UP_SUCCESS(200, "S_AUTH006", "회원가입에 성공하였습니다."),

    // Member
    GET_MEMBERS_SUCCESS(200, "S_MEMBER001", "유저 목록 조회에 성공하였습니다."),
    GET_LOGIN_USER_INFO_SUCCESS(200, "S_MEMBER002", "현재 로그인한 유저 정보 조회에 성공하였습니다."),
    GET_USER_INFO_SUCCESS(200, "S_MEMBER003", "유저 정보 조회에 성공하였습니다."),
    DELETE_USER_SUCCESS(200, "S_MEMBER004", "유저 삭제에 성공하였습니다."),
    GET_NICKNAME_DUPLICATE_SUCCESS(200, "S_MEMBER005", "닉네임 중복 여부 확인에 성공하였습니다."),
    UPDATE_MEMBER_INFO_SUCCESS(200, "S_MEMBER006", "유저 정보 수정에 성공하였습니다."),
    GET_MY_FEED_SUCCESS(200, "S_MEMBER007", "내 피드 조회에 성공하였습니다."),
    GET_MEMBER_FEED_SUCCESS(200, "S_MEMBER008", "유저 피드 조회에 성공하였습니다."),
    GET_MY_INFO_SUCCESS(200, "S_MEMBER009", "내 정보 조회에 성공하였습니다."),

    // Place
    GET_PLACES_SUCCESS(200, "S_PLACE001", "장소 목록 조회에 성공하였습니다."),
    GET_PLACE_DETAIL_SUCCESS(200, "S_PLACE002", "장소 상세 조회에 성공하였습니다."),

    // Review
    CREATE_REVIEW_SUCCESS(201, "S_REVIEW001", "리뷰 등록에 성공하였습니다."),
    GET_REVIEW_DETAIL_SUCCESS(200, "S_REVIEW002", "리뷰 상세 조회에 성공하였습니다."),
    GET_PHOTO_REVIEW_SUCCESS(200, "S_REVIEW003", "포토 리뷰 목록 조회에 성공하였습니다."),
    GET_TEXT_REVIEW_SUCCESS(200, "S_REVIEW004", "텍스트 리뷰 목록 조회에 성공하였습니다."),
    UPDATE_REVIEW_SUCCESS(200, "S_REVIEW005", "리뷰 수정에 성공하였습니다."),
    DELETE_REVIEW_SUCCESS(200, "S_REVIEW006", "리뷰 삭제에 성공하였습니다."),

    // Friend
    REQUEST_PIN_BUDDY_SUCCESS(201, "S_FRIEND001", "핀버디 신청이 완료되었습니다."),
    ACCEPT_PIN_BUDDY_SUCCESS(200, "S_FRIEND002", "핀버디 신청을 수락했습니다."),
    REJECT_PIN_BUDDY_SUCCESS(200, "S_FRIEND003", "핀버디 신청을 거절했습니다."),
    CANCEL_PIN_BUDDY_SUCCESS(200, "S_FRIEND004", "핀버디 신청을 취소했습니다."),
    REMOVE_PIN_BUDDY_SUCCESS(200, "S_FRIEND005", "핀버디를 삭제하였습니다."),
    GET_RECEIVED_PIN_BUDDY_REQUEST_LIST_SUCCESS(200, "S_FRIEND006", "받은 핀버디 신청 목록을 조회하였습니다."),
    GET_MY_PIN_BUDDY_LIST_SUCCESS(200, "S_FRIEND007", "나의 핀버디 목록을 조회하였습니다."),
    GET_USER_PIN_BUDDY_LIST_SUCCESS(200, "S_FRIEND008", "해당 유저의 핀버디 목록을 조회하였습니다."),
    GET_MY_PIN_BUDDY_INFO_SUCCESS(200, "S_FRIEND009", "나의 핀버디 정보를 조회하였습니다."),
    GET_SENT_PIN_BUDDY_REQUEST_LIST_SUCCESS(200, "S_FRIEND010", "보낸 핀버디 신청 목록을 조회하였습니다."),

    // Alarm
    GET_ALARMS_SUCCESS(200, "S_ALARM001", "알림 목록을 조회하였습니다."),
    GET_ALARM_DETAIL_SUCCESS(200, "S_ALARM002", "알림 상세 내용을 조회하였습니다."),

    // Article
    CREATE_ARTICLE_SUCCESS(201, "S_ARTICLE001", "에디터 아티클을 생성하였습니다."),
    GET_ARTICLE_DETAIL_SUCCESS(200, "S_ARTICLE002", "에디터 아티클을 조회하였습니다."),
    GET_ARTICLE_LIST_SUCCESS(200, "S_ARTICLE003", "에디터 아티클 목록을 조회하였습니다."),

    //BookMark
    CREATE_BOOKMARK_SUCCESS(201, "S_BOOKMARK001", "북마크 등록에 성공하였습니다."),
    GET_MY_BOOKMARK_SUCCESS(200, "S_BOOKMARK002", "내 북마크 목록 조회에 성공하였습니다."),
    DELETE_BOOKMARK_SUCCESS(200, "S_BOOKMARK003", "북마크 삭제에 성공하였습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
