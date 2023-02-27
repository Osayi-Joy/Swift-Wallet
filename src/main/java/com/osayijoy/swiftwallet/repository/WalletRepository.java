package com.osayijoy.swiftwallet.repository;

import com.osayijoy.swiftwallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> getAccountByAccountNumber(long accountNumber);
}
