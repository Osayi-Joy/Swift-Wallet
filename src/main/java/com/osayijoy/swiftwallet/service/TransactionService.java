package com.osayijoy.swiftwallet.service;


import com.osayijoy.swiftwallet.dtos.request.DepositAccountRequestDto;
import com.osayijoy.swiftwallet.dtos.request.TransferFundRequestDto;
import com.osayijoy.swiftwallet.dtos.request.WithdrawFundRequestDto;
import com.osayijoy.swiftwallet.dtos.response.DepositResponseDto;
import com.osayijoy.swiftwallet.dtos.response.TransferResponseDto;
import com.osayijoy.swiftwallet.dtos.response.WithdrawFundResponseDto;

public interface TransactionService {
    DepositResponseDto depositFunds(DepositAccountRequestDto depositAccountRequestDto);
    TransferResponseDto transferFunds(TransferFundRequestDto transferFundRequestDto);
    WithdrawFundResponseDto withdrawFunds(WithdrawFundRequestDto withdrawFundRequestDto);
}
