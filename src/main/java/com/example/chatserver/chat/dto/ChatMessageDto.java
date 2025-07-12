package com.example.chatserver.chat.dto;

public record ChatMessageDto(
        String message,
        String senderEmail
) {
}
