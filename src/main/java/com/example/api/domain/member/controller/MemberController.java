package com.example.api.domain.member.controller;

import com.example.api.domain.member.dto.MemberRequest;
import com.example.api.domain.member.dto.MemberResponse;
import com.example.api.domain.member.service.MemberService;
import com.example.api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(memberService.findAll()));
    }

    @GetMapping("/{userid}")
    public ResponseEntity<ApiResponse<MemberResponse>> findByUserid(@PathVariable String userid) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.findByUserid(userid)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.search(keyword)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> create(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(memberService.create(request)));
    }

    @PutMapping("/{userid}")
    public ResponseEntity<ApiResponse<MemberResponse>> update(
            @PathVariable String userid,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.update(userid, request)));
    }

    @DeleteMapping("/{userid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String userid) {
        memberService.delete(userid);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
