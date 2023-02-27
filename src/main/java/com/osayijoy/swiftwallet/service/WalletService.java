package com.osayijoy.swiftwallet.service;


import com.osayijoy.swiftwallet.dtos.request.ActivateAccountRequestDto;
import com.osayijoy.swiftwallet.dtos.response.FetchAccountResponseDto;
import com.osayijoy.swiftwallet.entity.Wallet;

public interface WalletService {
    FetchAccountResponseDto fetchAccount(long accountNumber);
    boolean activateAccount(ActivateAccountRequestDto activateAccountRequestDto);
    Wallet getLoggedInUserAccountDetails();

}
