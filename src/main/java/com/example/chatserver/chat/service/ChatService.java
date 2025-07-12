package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.dto.MyChatListResDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member sender = memberRepository.findByEmail(chatMessageDto.senderEmail())
                .orElseThrow(()-> new EntityNotFoundException("member cannot be found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageDto.message())
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

    @Transactional
    public void addParticipantToGroupChat(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("그룹채팅이 아닙니다.");
        }

        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if(!participant.isPresent()){
            addParticipantToRoom(chatRoom, member);
        }
    }

    private void addParticipantToRoom(ChatRoom chatRoom, Member member){
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getChatHistory(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

        if (chatParticipants.stream().noneMatch(cp -> cp.getMember().equals(member))) {
            throw new IllegalArgumentException("Member is not a participant of this chat room.");
        }

        return chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom).stream()
                .map(ChatMessageDto::from)
                .toList();
    }

    @Transactional
    public boolean isRoomPaticipant(String email, Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        return chatParticipants.stream().anyMatch(cp -> cp.getMember().equals(member));
    }

    @Transactional
    public void messageRead(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        for(ReadStatus readStatus : readStatuses){
            readStatus.updateIsRead(true);
        }
    }

    @Transactional(readOnly = true)
    public List<MyChatListResDto> getMyChatRooms(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        return chatParticipants.stream()
                .map(c -> {
                    long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
                    return MyChatListResDto.of(c, count);
                })
                .toList();
    }

    @Transactional
    public void leaveGroupChatRoom(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
        }

        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(()->new EntityNotFoundException("참여자를 찾을 수 없습니다."));
        chatParticipantRepository.delete(chatParticipant);
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        if(chatParticipants.isEmpty()){
            chatRoomRepository.delete(chatRoom);
        }
    }
}
