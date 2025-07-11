package com.example.chatserver.member.dto;

import com.example.chatserver.member.domain.Member;

public record MemberListResDto(
        long id,
        String name,
        String email
) {

    public static MemberListResDto from(Member member) {
        return new MemberListResDto(
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }
}
