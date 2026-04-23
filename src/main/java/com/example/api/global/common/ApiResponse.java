package com.example.api.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.api.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "응답 데이터")
    private final T data;

    @Schema(description = "오류 정보")
    private final ErrorResponse error;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return error(errorCode, errorCode.getMessage());
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(errorCode.name(), message));
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Schema(description = "API 오류 응답")
    public static class ErrorResponse {

        @Schema(description = "오류 코드", example = "MEMBER_NOT_FOUND")
        private final String code;

        @Schema(description = "오류 메시지", example = "회원을 찾을 수 없습니다.")
        private final String message;
    }
}
