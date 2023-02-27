package com.zurum.lanefinance.controller;


import com.zurum.lanefinance.dtos.request.LogInRequest;
import com.zurum.lanefinance.dtos.request.UserRegistrationRequestDto;
import com.zurum.lanefinance.dtos.response.AppResponse;
import com.zurum.lanefinance.dtos.response.RegistrationResponseDto;
import com.zurum.lanefinance.dtos.response.TokenResponse;
import com.zurum.lanefinance.exceptions.CustomException;
import com.zurum.lanefinance.service.UserService;
import com.zurum.lanefinance.utils.SecurityUtil;
import com.zurum.lanefinance.utils.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static com.zurum.lanefinance.constants.SecurityConstants.PASSWORD_NOT_MATCH_MSG;
import static com.zurum.lanefinance.utils.StringUtil.doesBothStringMatch;

@Slf4j
@Tag(name = "Authentication Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/register")
    @Operation(summary = "Register New User", responses = {
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegistrationResponseDto.class)))})
    public ResponseEntity<AppResponse<?>> registerUser(@RequestBody @Valid final UserRegistrationRequestDto registrationRequestDto) {
        log.info("controller register: register user :: [{}] ::", registrationRequestDto.getEmail());
        validateUserRegistrationDto(registrationRequestDto);
        RegistrationResponseDto response = userService.registerUser(registrationRequestDto);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/register").toUriString());
        return ResponseEntity.created(uri).body(AppResponse.buildSuccess(response));
    }

    private void validateUserRegistrationDto(UserRegistrationRequestDto request) {
        log.info("validating user registration request for email :: {}", request.getEmail());
        if (!doesBothStringMatch(request.getConfirmPassword(), request.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH_MSG, HttpStatus.BAD_REQUEST);
        }
        List<String> roleEnum = List.of("USER","ADMIN","STAFF","VENDOR");

        String role = request.getRole();
        if (role != null) {
            role = role.trim().toUpperCase();
            if (!roleEnum.contains(role)) {
                throw new CustomException("Invalid role, Options includes: USER, ADMIN, STAFF, VENDOR ");
            }
        }
        log.info("successful validation for user registration request for email :: {}", request.getEmail());
    }


    @PostMapping("/login")
    @Operation(summary = "Login User", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponse.class)))})
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LogInRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if (authentication.isAuthenticated()) {
            TokenResponse response = SecurityUtil.generateToken(authentication);
            return ResponseEntity.status(200).body(AppResponse.builder().statusCode("00").result(response).message("Authenticated").build());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }
}
