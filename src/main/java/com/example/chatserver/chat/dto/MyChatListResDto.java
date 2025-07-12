package com.example.chatserver.chat.dto;

import com.example.chatserver.chat.domain.ChatParticipant;

public record MyChatListResDto(
        long roomId,
        String roomName,
        String isGroupChat,
        long unReadCount
) {

    public static MyChatListResDto of(ChatParticipant chatParticipant, long count) {
        return new MyChatListResDto(
                chatParticipant.getChatRoom().getId(),
                chatParticipant.getChatRoom().getName(),
                chatParticipant.getChatRoom().getIsGroupChat(),
                count
        );
    }
}
