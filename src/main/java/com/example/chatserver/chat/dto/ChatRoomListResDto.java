package com.example.chatserver.chat.dto;

import com.example.chatserver.chat.domain.ChatRoom;

public record ChatRoomListResDto(
        long roomId,
        String roomName
) {

    public static ChatRoomListResDto from(ChatRoom chatRoom) {
        return new ChatRoomListResDto(
                chatRoom.getId(),
                chatRoom.getName()
        );
    }
}
