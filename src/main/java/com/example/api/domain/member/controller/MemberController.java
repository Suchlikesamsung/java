package com.example.api.domain.member.controller;

import com.example.api.domain.member.dto.MemberRequest;
import com.example.api.domain.member.dto.MemberResponse;
import com.example.api.domain.member.service.MemberService;
import com.example.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 목록 조회", description = "전체 회원 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(memberService.findAll()));
    }

    @Operation(summary = "회원 단건 조회", description = "사용자 아이디로 회원을 조회합니다.")
    @GetMapping("/{userid}")
    public ResponseEntity<ApiResponse<MemberResponse>> findByUserid(
            @Parameter(description = "사용자 아이디", example = "hong") @PathVariable String userid) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.findByUserid(userid)));
    }

    @Operation(summary = "회원 검색", description = "키워드로 회원을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> search(
            @Parameter(description = "검색 키워드", example = "홍길동") @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.search(keyword)));
    }

    @Operation(summary = "회원 생성", description = "새 회원을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> create(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(memberService.create(request)));
    }

    @Operation(summary = "회원 수정", description = "사용자 아이디에 해당하는 회원 정보를 수정합니다.")
    @PutMapping("/{userid}")
    public ResponseEntity<ApiResponse<MemberResponse>> update(
            @Parameter(description = "사용자 아이디", example = "hong") @PathVariable String userid,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.update(userid, request)));
    }

    @Operation(summary = "회원 삭제", description = "사용자 아이디에 해당하는 회원을 삭제합니다.")
    @DeleteMapping("/{userid}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "사용자 아이디", example = "hong") @PathVariable String userid) {
        memberService.delete(userid);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
