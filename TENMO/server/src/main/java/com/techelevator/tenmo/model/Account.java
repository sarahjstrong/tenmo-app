package com.techelevator.tenmo.model;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;


public class Account {

        private int accountId;        // Unique identifier for the account //
        private int userId; // Identifier of the user associated with the account //



    private String username;

        @Positive(message = "Balance must be greater than 0")
        private BigDecimal balance = new BigDecimal(1000);  // Current balance of the account - BigDecimal :( //

        // Default constructor//
        public Account(String username) {
            this.username = username;
        }

        public Account(int accountId, int userId, String username) {
            this.accountId = accountId;
            this.userId = userId;
            this.username = username;
            this.balance = new BigDecimal(1000);
        }



    // Getters and Setters //
        public int getAccountId() {
            return accountId;
        }

         public void setUsername(String username) {
        this.username = username;
          }
         public String getUsername() {
              return username;
         }
        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId && userId == account.userId && balance.equals(account.balance);
    }



    // toString method for representing the Account obj as a string //
        @Override
        public String toString() {
            return "Account{" +
                    "accountId=" + accountId +
                    ", userId=" + userId +
                    ", balance=" + balance +
                    '}';
        }

    }


