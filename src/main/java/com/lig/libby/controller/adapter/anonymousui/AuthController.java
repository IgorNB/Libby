package com.lig.libby.controller.adapter.anonymousui;

import com.lig.libby.controller.adapter.anonymousui.dto.AuthResponseDto;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.security.jwt.TokenProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(AuthController.FORM_AUTH_REQUEST_MAPPING)
public class AuthController {
    public static final String FORM_AUTH_REQUEST_MAPPING = "/auth";

    private final AuthenticationManager authenticationManager;
    private final TokenProviderService tokenProviderService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, TokenProviderService tokenProviderService) {
        this.authenticationManager = authenticationManager;
        this.tokenProviderService = tokenProviderService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProviderService.createToken(authentication, false);
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

}
