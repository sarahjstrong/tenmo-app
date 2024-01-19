package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountBalanceRequestDTO;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface AccountDao {


    boolean moveMoney(Transfer transfer);

    AccountBalanceRequestDTO getAccountBalance(String username);


    Account createAccount(String username);

    Account getAccountById(int newId);
}
