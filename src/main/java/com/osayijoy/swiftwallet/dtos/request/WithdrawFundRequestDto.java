package com.zurum.lanefinance.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawFundRequestDto {
    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;
}
