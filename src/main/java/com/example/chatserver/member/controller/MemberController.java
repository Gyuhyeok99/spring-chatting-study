package com.example.chatserver.member.controller;

import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.dto.MemberSaveResDto;
import com.example.chatserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<MemberSaveResDto> memberCreate(
            @RequestBody MemberSaveReqDto memberSaveReqDto){
        MemberSaveResDto memberSaveResDto = memberService.create(memberSaveReqDto);
        return new ResponseEntity<>(memberSaveResDto, HttpStatus.CREATED);
    }
}
