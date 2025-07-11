package com.example.chatserver.member.dto;

public record MemberSaveResDto(
        long id
) {

    public static MemberSaveResDto of(long id) {
        return new MemberSaveResDto(id);
    }
}
