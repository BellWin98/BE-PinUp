package com.pinup.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberRelationType {
    SELF,
    FRIEND,
    PENDING,
    STRANGER
}