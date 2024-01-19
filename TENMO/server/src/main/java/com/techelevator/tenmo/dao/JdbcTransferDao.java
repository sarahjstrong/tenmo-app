package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@PreAuthorize("isAuthenticated()")
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // I'm getting a null pointer exception line 29 when trying to use the get method on this path via postman
    @Override
    public List<User> showUsers(String username) {
        List<User> users = new ArrayList<>();

        try {
            String sql = "SELECT username FROM tenmo_user;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

            while (results.next()) {
                User currentUser = new User();
                if (!results.getString("username").equals(username)) {
                    currentUser.setUsername(results.getString("username"));
                    users.add(currentUser);
                }
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return users;
    }

    // Questions: How do I do this considering the sender information is not a parameter? I tried a placeholder in the new transfer object.
    // Should our transfer table include both the sender/receiver username AND id or is just the username okay?
    @Override
    public Transfer newTransfer(Transfer transfer) {
        try {

            String transferSql = "INSERT INTO transfer(sender_username, receiver_username, amount)\n" +
                                "VALUES (?, ?, ?) RETURNING transfer_id;";
            int newTransferId = jdbcTemplate.queryForObject(transferSql, int.class, transfer.getSenderUsername(), transfer.getReceiverUsername(), transfer.getAmount());
            transfer.setId(newTransferId);
            return transfer;

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public List<Transfer> showTransfers(String username) {
        List<Transfer> transfers = new ArrayList<>();

        try {
            String sql = "SELECT transfer_id, sender_username, receiver_username, amount FROM transfer WHERE sender_username = ? OR receiver_username = ?;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, username);

            while (results.next()) {
                Transfer currentTransfer = new Transfer(results.getInt("transfer_id"), results.getString("sender_username"), results.getString("receiver_username"), results.getBigDecimal("amount"));
                transfers.add(currentTransfer);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return transfers;
    }

    @Override
    public Transfer getTransferById(int id) {

        try {
            String sql = "SELECT transfer_id, sender_username, receiver_username, amount FROM transfer WHERE transfer_id = ?;";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()) {
                Transfer thisTransfer = new Transfer(result.getInt("transfer_id"), result.getString("sender_username"), result.getString("receiver_username"), result.getBigDecimal("amount"));
                return thisTransfer;
            }
            return null;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

    }

}
