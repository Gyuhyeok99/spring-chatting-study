package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room/group/create")
    public ResponseEntity<Void> createGroupRoom(
            @RequestParam String roomName){
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }
}
