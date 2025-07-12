package com.example.chatserver.chat.dto;

import com.example.chatserver.chat.domain.ChatMessage;

public record ChatMessageDto(
        String message,
        String senderEmail
) {

    public static ChatMessageDto from(ChatMessage chatMessage) {
        return new ChatMessageDto(
                chatMessage.getContent(),
                chatMessage.getMember().getEmail()
        );
    }
}
