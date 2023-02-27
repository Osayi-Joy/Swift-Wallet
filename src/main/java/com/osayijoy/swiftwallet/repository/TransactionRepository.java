package com.osayijoy.swiftwallet.repository;

import com.osayijoy.swiftwallet.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionLog, String> {

}
