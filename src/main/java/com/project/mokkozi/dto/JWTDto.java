package com.project.mokkozi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class JWTDto {
    public String accessToken;
    public String refreshToken;
}
