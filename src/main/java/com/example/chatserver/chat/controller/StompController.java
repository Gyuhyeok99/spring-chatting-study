package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

//    @MessageMapping("/{roomId}") // //클라이언트에서 특정 publish/roomId형태로 메시지를 발행시 MessageMapping 수신
//    @SendTo("/topic/{roomId}") // 해당 roomId에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
//    // DestinationVariable : @MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용
//    public String sendMessage(@DestinationVariable Long roomId, String message) {
//        log.info("Received message for room {}: {}", roomId, message);
//        return message;
//    }

    @MessageMapping("/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @RequestBody ChatMessageDto chatMessageDto) {
        log.info("Received message for room {}: {}", roomId, chatMessageDto.message());
        chatService.saveMessage(roomId, chatMessageDto);
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);
    }
}
