package com.project.mokkozi.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class JWTDto {
    public String accessToken;
    public String refreshToken;
}
