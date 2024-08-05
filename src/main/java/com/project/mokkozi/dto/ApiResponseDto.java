package com.project.mokkozi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Getter
public class ApiResponseDto<T> {
    private HttpStatus resultCode;
    private String resultMsg;
    private T resultData;

    public ApiResponseDto(final HttpStatus resultCode, final String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.resultData = null;
    }
    public static<T> ApiResponseDto<T> res(final HttpStatus resultCode, final String resultMsg) {
        return res(resultCode, resultMsg, null);
    }
    public static<T> ApiResponseDto<T> res(final HttpStatus resultCode, final String resultMsg, final T t) {
        return ApiResponseDto.<T>builder()
                .resultData(t)
                .resultCode(resultCode)
                .resultMsg(resultMsg)
                .build();
    }
}