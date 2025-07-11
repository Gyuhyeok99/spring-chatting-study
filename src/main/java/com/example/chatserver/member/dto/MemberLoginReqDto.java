package com.example.chatserver.member.dto;

public record MemberLoginReqDto(
        String email,
        String password
) {
}
