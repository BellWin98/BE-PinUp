package com.pinup.domain.member.dto.response;

import com.pinup.domain.member.entity.MemberRelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchMemberResponse {

    @Schema(description = "유저 개인정보")
    private MemberResponse memberResponse;

    @Schema(description = "관계")
    private MemberRelationType relationType;
}
