package com.project.mokkozi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JWTDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
