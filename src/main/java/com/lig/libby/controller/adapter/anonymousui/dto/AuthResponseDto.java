package com.lig.libby.controller.adapter.anonymousui.dto;

import lombok.Getter;

@Getter
public class AuthResponseDto {
    private final String accessToken;
    private final String tokenType;

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
