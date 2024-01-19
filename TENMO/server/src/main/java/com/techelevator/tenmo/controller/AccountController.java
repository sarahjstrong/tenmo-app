package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountBalanceRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/create-account", method = RequestMethod.POST)
    public Account createAccount(Principal principal) {
        return accountDao.createAccount(principal.getName());
    }

    @RequestMapping(path = "/get-balance", method = RequestMethod.GET)
    public AccountBalanceRequestDTO getBalance(Principal principal) {
        AccountBalanceRequestDTO accountBalance = accountDao.getAccountBalance(principal.getName());
        if (accountBalance == null) {
            throw new DaoException();
        } else {
            return accountBalance;
        }
    }



}
