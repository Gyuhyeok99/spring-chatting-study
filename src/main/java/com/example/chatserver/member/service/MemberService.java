package com.example.chatserver.member.service;


import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.dto.MemberSaveResDto;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    @Transactional
    public Map<String, Object> login(MemberLoginReqDto memberLoginReqDto){
        Member member = memberRepository.findByEmail(memberLoginReqDto.email())
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 이메일입니다."));
        if(!passwordEncoder.matches(memberLoginReqDto.password(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return loginInfo;
    }

    @Transactional(readOnly = true)
    public List<MemberListResDto> findAll(){
        return memberRepository.findAll().stream()
                .map(MemberListResDto::from)
                .toList();
    }
}
