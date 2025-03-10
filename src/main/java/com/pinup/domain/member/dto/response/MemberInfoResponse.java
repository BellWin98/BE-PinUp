package com.pinup.domain.member.dto.response;

import com.pinup.domain.member.entity.MemberRelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "유저 정보 조회 응답 DTO")
public class MemberInfoResponse {

    @Schema(description = "유저 개인정보")
    private MemberResponse memberResponse;

    @Schema(description = "관계")
    private MemberRelationType relationType;
}
