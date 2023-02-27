package com.zurum.lanefinance.controller;

import com.zurum.lanefinance.dtos.request.EmailDto;
import com.zurum.lanefinance.dtos.response.AppResponse;
import com.zurum.lanefinance.service.SendMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@Tag(name = "Email Controller")
@AllArgsConstructor
@RequestMapping("mail")
public class EmailController {

    private final SendMailService sendMailService;

    @PostMapping()
    @Operation(summary = "Send Email",
            security = {@SecurityRequirement(name = "bearer-token")},
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Boolean.class)))})
    public ResponseEntity<AppResponse<Boolean>> sendMail(@RequestBody @Valid final EmailDto emailDto) {
        log.info("email controller -: sending email to [{}]", emailDto.getRecipient());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> sendMailService.sendEmail(emailDto));

        return
                future.isDone() ?
                        ResponseEntity.ok(AppResponse.<Boolean>builder()
                                .isSuccessful(true)
                                .message("email sent successfully")
                                .result(true)
                                .build()
                        )
                        :
                        ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(AppResponse.<Boolean>builder()
                                .isSuccessful(false)
                                .message("email Not successful")
                                .result(false)
                                .build()
                        );

    }
}
