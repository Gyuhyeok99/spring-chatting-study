package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.dto.MyChatListResDto;
import com.example.chatserver.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/room/group/list")
    public ResponseEntity<List<ChatRoomListResDto>> getGroupChatRooms(){
        List<ChatRoomListResDto> chatRooms = chatService.getGroupchatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    @PostMapping("/room/private/create")
    public ResponseEntity<Long> getOrCreatePrivateRoom(
            @RequestParam("otherMemberId") Long otherMemberId){
        Long roomId = chatService.getOrCreatePrivateRoom(otherMemberId);
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }

    @PostMapping("/room/group/create")
    public ResponseEntity<Void> createGroupRoom(
            @RequestParam String roomName){
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<Void> joinGroupChatRoom(
            @PathVariable("roomId") Long roomId){
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(
            @PathVariable("roomId") Long roomId){
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }

    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(
            @PathVariable("roomId") Long roomId){
        chatService.messageRead(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my/rooms")
    public ResponseEntity<List<MyChatListResDto>> getMyChatRooms(){
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms();
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }

    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<Void> leaveGroupChatRoom(
            @PathVariable("roomId") Long roomId){
        chatService.leaveGroupChatRoom(roomId);
        return ResponseEntity.ok().build();
    }
}
