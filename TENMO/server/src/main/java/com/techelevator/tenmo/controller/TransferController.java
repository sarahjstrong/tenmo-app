package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    private AccountDao accountDao;

    public TransferController(TransferDao transferDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/display-users", method = RequestMethod.GET)
    public List<User> showUsers(Principal principal) {
        List<User> allUsers = transferDao.showUsers(principal.getName());
        
        return allUsers;
    }

    // Updated database sql script
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    // Transfer item contains the defined properties of the amount and the recipient name. The sender if defined in this method
    // using principal
    public Transfer transfer(@Valid @RequestBody Transfer transfer, Principal principal) {
        if (!transfer.getReceiverUsername().equals(principal.getName())) {
            transfer.setSenderUsername(principal.getName());
            boolean status = accountDao.moveMoney(transfer);

            if (status) {
                Transfer newTransfer = transferDao.newTransfer(transfer); // just passes in transfer
                if (newTransfer == null) {
                    throw new DaoException("Error creating transfer");
                } else {
                    return newTransfer;
                }
            } else {
                throw new DaoException("Account error. Money could not be moved.");
            }
        } else {
            throw new DaoException("Sender and receiver must be different users");
        }
    }

    @RequestMapping(path = "/all-transfers", method = RequestMethod.GET)
    public List<Transfer> allTransfers(Principal principal) {
        return transferDao.showTransfers(principal.getName());
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id) {
        return transferDao.getTransferById(id);
    }
}
