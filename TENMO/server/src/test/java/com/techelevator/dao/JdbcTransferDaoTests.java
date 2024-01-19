package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JdbcTransferDaoTests extends BaseDaoTests {
    // Static variables for users
    private static final User BOB = new User(1001, "bob", "$2a$10$G/MIQ7pUYupiVi72DxqHquxl73zfd7ZLNBoB2G6zUb.W16imI2.W2", "user");
    private static final User USER = new User(1002, "user", "$2a$10$Ud8gSvRS4G1MijNgxXWzcexeXlVs4kWDOkjE7JFIkNLKEuE57JAEy", "user");
    private static final Transfer TEST_TRANSFER = new Transfer(0, "bob", "user", new BigDecimal(100.00));
    private static final Transfer TRANSFER_1 = new Transfer(3001, "bob", "user", new BigDecimal(100));
    private static final Transfer TRANSFER_2 = new Transfer(3002, "user", "bob", new BigDecimal(200));

    private JdbcTransferDao sut;


    @Before
    public void setup() {
        // Need help with set and loading accounts before
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }

    // Makes sure the list size of all user is correct (this excluded the current logged in user so count should be one)
    // Tests using the username 'bob' that is provided using the Principal class in the controller
    @Test
    public void showUsers_returns_correct_users() {
        List<User> usersToTransferTo = sut.showUsers("bob");

        Assert.assertEquals(1, usersToTransferTo.size());
        assertUsersMatch(USER, usersToTransferTo.get(0));

    }

    @Test
    public void newTransfer_creates_transfer() {
        Transfer createdTransfer = sut.newTransfer(TEST_TRANSFER);
        Assert.assertNotNull(createdTransfer);

        int newId = createdTransfer.getId();
        Assert.assertTrue(newId > 3001);

        Transfer retrievedTransfer = sut.getTransferById(newId);
        assertTransfersMatch(createdTransfer, retrievedTransfer);
    }


    @Test
    public void showTransfers_returns_correct_transfers() {
        List<Transfer> allTransfers = sut.showTransfers("bob");

        Assert.assertEquals(2, allTransfers.size());
        assertTransfersMatch(TRANSFER_1, allTransfers.get(0));
        assertTransfersMatch(TRANSFER_2, allTransfers.get(1));
    }

    @Test
    public void getTransferById_returns_correct_transfer() {
        Transfer actual = sut.getTransferById(3001);
        assertTransfersMatch(TRANSFER_1, actual);
    }

    @Test
    public void getTransferById_with_invalid_id_returns_null_transfer() {
        Transfer invalidIdTransfer = sut.getTransferById(3005);
        Assert.assertNull(invalidIdTransfer);
    }

    private void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getSenderUsername(), actual.getSenderUsername());
        Assert.assertEquals(expected.getReceiverUsername(), actual.getReceiverUsername());
        Assert.assertTrue(expected.getAmount().compareTo(actual.getAmount()) == 0);
    }

    private void assertUsersMatch(User expected, User actual) {
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
    }
}
