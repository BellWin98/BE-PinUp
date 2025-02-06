package com.pinup.dto.request;

import lombok.Data;

@Data
public class MemberJoinRequest {
    private String email;
    private String name;
//    private String nickname;
    private String profileImageUrl;
    private String password;
}
