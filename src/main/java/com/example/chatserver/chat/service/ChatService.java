package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageReqDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveMessage(Long roomId, ChatMessageReqDto chatMessageReqDto){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member sender = memberRepository.findByEmail(chatMessageReqDto.senderEmail())
                .orElseThrow(()-> new EntityNotFoundException("member cannot be found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageReqDto.message())
                .build();
        chatMessageRepository.save(chatMessage);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant chatParticipant : chatParticipants){
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(chatParticipant.getMember())
                    .chatMessage(chatMessage)
                    .isRead(chatParticipant.getMember().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResDto> getGroupchatRooms(){
        return chatRoomRepository.findByIsGroupChat("Y").stream()
                .map(ChatRoomListResDto::from)
                .toList();
    }

    @Transactional
    public void createGroupRoom(String chatRoomName){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);

        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }
}
