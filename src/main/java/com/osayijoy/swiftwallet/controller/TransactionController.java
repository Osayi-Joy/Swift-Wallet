package com.zurum.lanefinance.controller;

import com.zurum.lanefinance.dtos.request.DepositAccountRequestDto;
import com.zurum.lanefinance.dtos.request.TransferFundRequestDto;
import com.zurum.lanefinance.dtos.request.WithdrawFundRequestDto;
import com.zurum.lanefinance.dtos.response.AppResponse;
import com.zurum.lanefinance.dtos.response.DepositResponseDto;
import com.zurum.lanefinance.dtos.response.TransferResponseDto;
import com.zurum.lanefinance.dtos.response.WithdrawFundResponseDto;
import com.zurum.lanefinance.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "Transaction Controller")
@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    //Logging @Slf4j @Log4j2  - info, warn, debug, error

    private final TransactionService transactionService;

    @PostMapping("/deposit-fund")
    @Operation(summary = "deposit funds",
            security = {@SecurityRequirement(name = "bearer-token")},
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DepositResponseDto.class)))})
    public ResponseEntity<AppResponse<DepositResponseDto>> depositFunds(@RequestBody @Valid final DepositAccountRequestDto depositAccountRequestDto) {
        log.info("controller depositFunds- for :: {}", depositAccountRequestDto.getReceiverAccountNumber() );
        DepositResponseDto response = transactionService.depositFunds(depositAccountRequestDto);
        return ResponseEntity.ok(AppResponse.buildSuccessTxn(response));
    }

    @PostMapping("/withdraw-fund")
    @Operation(summary = "withdraw funds",
            security = {@SecurityRequirement(name = "bearer-token")},
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = WithdrawFundResponseDto.class)))})
    public ResponseEntity<AppResponse<WithdrawFundResponseDto>> withdrawFunds(@RequestBody @Valid final WithdrawFundRequestDto withdrawFundRequestDto) {
        log.info("controller withdrawFunds- for :: [{}]", withdrawFundRequestDto.getAmount() );
        WithdrawFundResponseDto response = transactionService.withdrawFunds(withdrawFundRequestDto);
        return ResponseEntity.ok(AppResponse.buildSuccessTxn(response));
    }

    @PostMapping("/transfer-fund")
    @Operation(summary = "transfer funds",
            security = {@SecurityRequirement(name = "bearer-token")},
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransferResponseDto.class)))})
    public ResponseEntity<AppResponse<TransferResponseDto>> transferFunds(@RequestBody @Valid final TransferFundRequestDto transferFundRequestDto) {
        log.info("controller transferFunds- for :: [{}]", transferFundRequestDto.getReceiverAccountNumber() );
        TransferResponseDto response = transactionService.transferFunds(transferFundRequestDto);
        return ResponseEntity.ok(AppResponse.buildSuccessTxn(response));
    }

}
