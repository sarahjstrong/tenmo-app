package com.techelevator.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountBalanceRequestDTO;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountDaoTests extends BaseDaoTests {

    private static final AccountBalanceRequestDTO EXPECTED_BALANCE_REQUEST_DTO = new AccountBalanceRequestDTO("bob", new BigDecimal("1000.00"));
    private static final Transfer SUCCESS_TRANSFER_TEST = new Transfer(3003, "bob", "user", new BigDecimal(50));
    private static final Transfer INVALID_RECEIVER_TRANSFER = new Transfer(3003, "bob", "nope", new BigDecimal(50));
    private static final Transfer INVALID_AMOUNT_TRANSFER = new Transfer(3003, "bob", "user", new BigDecimal(5000));
    private static final Account TEST_ACCOUNT = new Account(2003, 1001, "bob");
    private static final BigDecimal SUCCESS_BOB_BALANCE = new BigDecimal(950);
    private static final BigDecimal SUCCESS_USER_BALANCE = new BigDecimal(1050);

    private static final Transfer FAIL_TRANSFER_TEST = new Transfer(3004, "bob", "user", new BigDecimal(5000));

    private AccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }


    @Test
    public void moveMoney_successful_transfer_returns_true_and_money_moves() {

        Assert.assertTrue("Transfer should be successful", sut.moveMoney(SUCCESS_TRANSFER_TEST));

        // print balances for debugging
        AccountBalanceRequestDTO bobAccount = sut.getAccountBalance("bob");
        BigDecimal bobActualBalance = bobAccount.getBalance();
        System.out.println("Bob's Actual Balance: " + bobActualBalance);

        AccountBalanceRequestDTO userAccount = sut.getAccountBalance("user");
        BigDecimal userActualBalance = userAccount.getBalance();
        System.out.println("User's Actual Balance: " + userActualBalance);
        // moveMoney method returns true for a successful transfer

        // Check that the balances match the expected values
        Assert.assertTrue("Bob's balance should match", bobActualBalance.compareTo(SUCCESS_BOB_BALANCE) == 0);
        Assert.assertTrue("User's balance should match", userActualBalance.compareTo(SUCCESS_USER_BALANCE) == 0);
    }

    @Test
    public void moveMoney_fails_with_nonexistent_receiver() {
        Assert.assertFalse("Method should return false if receiver does not exist: ", sut.moveMoney(INVALID_RECEIVER_TRANSFER));
    }

    @Test
    public void moveMoney_fails_when_sender_doesnt_have_enough_money() {
        Assert.assertFalse("Method should return false if sender does not have enough money: ", sut.moveMoney(INVALID_AMOUNT_TRANSFER));
    }



    @Test
    public void getAccountBalance_with_valid_username_returns_correct_balance_and_username() {
        // Get AccountBalanceRequestDTO for existing user
        AccountBalanceRequestDTO actual = sut.getAccountBalance("bob");
        // Assert the actual AccountBalanceRequestDTO matches expected result
        assertAccountBalanceRequestDTOMatch(EXPECTED_BALANCE_REQUEST_DTO, actual);
    }

    @Test
    public void getAccountBalance_with_invalid_username_returns_null() {
        // Request AccountBalanceRequestDTO for invalid user. (This issue is unlikely because we get the username using Principal in our controller)
        AccountBalanceRequestDTO invalidUserResult = sut.getAccountBalance("invalid");
        // Assert the account balance
        Assert.assertNull(invalidUserResult);
    }



    @Test
    public void createAccount_creates_new_account() {
        // Create a new account
        String username = "bob";
        Account result = sut.createAccount(username);
        // Assert the created account
        assertAccountsMatch(TEST_ACCOUNT, result);
    }


    private void assertAccountsMatch(Account expected, Account actual) {
        Assert.assertEquals("Account ID should match", expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals("User ID should match", expected.getUserId(), actual.getUserId());
        Assert.assertEquals("Username should match", expected.getUsername(), actual.getUsername());
    }


    private void assertAccountBalanceRequestDTOMatch(AccountBalanceRequestDTO expected, AccountBalanceRequestDTO actual) {
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getBalance(), actual.getBalance());
    }



}
