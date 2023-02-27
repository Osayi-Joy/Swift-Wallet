package com.osayijoy.swiftwallet.dtos.response;

import com.osayijoy.swiftwallet.constants.WalletType;
import lombok.Data;

@Data
public class RegistrationResponseDto {
    private Long newAccountNumber;
    private String accountType = WalletType.SAVINGS.toString();

    public RegistrationResponseDto(Long newAccountNumber) {
        this.newAccountNumber = newAccountNumber;
    }
}
