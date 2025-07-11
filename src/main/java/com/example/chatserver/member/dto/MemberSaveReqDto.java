package com.example.chatserver.member.dto;

public record MemberSaveReqDto(
        String name,
        String email,
        String password
) {
}
