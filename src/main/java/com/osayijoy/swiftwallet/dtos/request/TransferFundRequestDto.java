package com.zurum.lanefinance.dtos.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class TransferFundRequestDto {

    @NotNull
    private Long receiverAccountNumber;

    @NotNull(message = "account cannot be empty")
    @Min(value = 0)
    private BigDecimal amount;

    @NotBlank(message = "sender cannot be blank")
    private String sender;

    @NotBlank(message = "receiver cannot be blank")
    private String receiver;


}
