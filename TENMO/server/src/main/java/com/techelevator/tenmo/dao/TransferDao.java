package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    List<User> showUsers(String username);

    Transfer newTransfer(Transfer transfer);

    List<Transfer> showTransfers(String username);

    Transfer getTransferById(int id);
}
