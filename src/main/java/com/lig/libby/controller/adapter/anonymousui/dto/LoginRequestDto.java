package com.lig.libby.controller.adapter.anonymousui.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
