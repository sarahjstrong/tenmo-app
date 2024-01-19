package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountBalanceRequestDTO;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {
    private  JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public  boolean moveMoney(Transfer transfer) {
        //sql to update the sender and receiver balances//
        int totalRowsUpdated = 0;
        try {
            String senderBalanceSql = "SELECT balance FROM account WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
            BigDecimal senderBalance = jdbcTemplate.queryForObject(senderBalanceSql, BigDecimal.class, transfer.getSenderUsername());
            if (senderBalance.compareTo(transfer.getAmount()) == 0 || senderBalance.compareTo(transfer.getAmount()) == 1) {
                String updateSenderBalanceSql = "UPDATE account SET balance = balance - ? WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
                int senderRowUpdated = jdbcTemplate.update(updateSenderBalanceSql, transfer.getAmount(), transfer.getSenderUsername());
                String updateReceiverBalance = "UPDATE account SET balance = balance + ? WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
                int receiverRowUpdated = jdbcTemplate.update(updateReceiverBalance, transfer.getAmount(), transfer.getReceiverUsername());
                totalRowsUpdated = senderRowUpdated + receiverRowUpdated;
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        } catch (DataAccessException e) {
            return false;
        }

        if (totalRowsUpdated == 2) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public AccountBalanceRequestDTO getAccountBalance(String username) {
        // Get the account balance for the user with the provided user ID//
        try {

            String sql = "SELECT balance, username FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id" +
                    " WHERE username = ?;";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);


            if (result.next() ) {
                AccountBalanceRequestDTO accountBalanceRequestDTO = new AccountBalanceRequestDTO(result.getString("username"),
                        result.getBigDecimal("balance"));
                return accountBalanceRequestDTO;
            } else {
                return null;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }


    @Override
    public Account createAccount(String username) {
        // Create a new account for the user with the provided user ID//
        try {
            Account newAccount = new Account(username);
            String userIdSql = "SELECT user_id FROM tenmo_user WHERE username = ?;";
            SqlRowSet result = jdbcTemplate.queryForRowSet(userIdSql, username);
            if (result.next()) {
                int userId = result.getInt("user_id");
                System.out.println(userId);
                String sql = "INSERT INTO account (user_id, balance) VALUES (?, ?) RETURNING account_id";
                int accountId = jdbcTemplate.queryForObject(sql, int.class, userId, newAccount.getBalance());
                newAccount.setAccountId(accountId);
                newAccount.setUserId(userId);
                return newAccount;
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return null;
    }

    @Override
    public Account getAccountById(int newId) {
        return null;
    }
}



