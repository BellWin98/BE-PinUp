package com.pinup.global.response;

import lombok.Getter;

@Getter
public class ResultResponse {

    private final int status; // HTTP 상태코드
    private final String code; // Business 코드
    private final String message; // 응답 메세지
    private final Object data; // 응답 데이터

    public ResultResponse(ResultCode resultCode, Object data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static ResultResponse of(ResultCode resultCode, Object data) {
        return new ResultResponse(resultCode, data);
    }

    public static ResultResponse of(ResultCode resultCode) {
        return new ResultResponse(resultCode, null);
    }
}
