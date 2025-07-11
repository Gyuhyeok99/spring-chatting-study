package com.example.chatserver.member.service;


import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.dto.MemberSaveResDto;
import com.example.chatserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberSaveResDto create(MemberSaveReqDto memberSaveReqDto){
        if(memberRepository.findByEmail(memberSaveReqDto.email()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.name())
                .email(memberSaveReqDto.email())
                .password(passwordEncoder.encode(memberSaveReqDto.password()))
                .build();
        Member member = memberRepository.save(newMember);
        return MemberSaveResDto.of(member.getId());
    }
}
