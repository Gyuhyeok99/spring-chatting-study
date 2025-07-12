package com.example.chatserver.chat.dto;

public record ChatMessageReqDto(
        String message,
        String senderEmail
) {
}
