package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {

    // Properties
    private int id;
    private String senderUsername;
    @NotBlank(message = "Recipient must be provided")
    private String receiverUsername;
    @Positive(message = "Amount must be greater than 0")
    @NotNull(message = "Amount must be provided")
    private BigDecimal amount;
    @JsonIgnore
    private boolean approved = true;

    // Constructors
    public Transfer() { }

    public Transfer(int id, String senderUsername, String receiverUsername, BigDecimal amount) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.amount = amount;
    }

    // Getters and setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    // toString
    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", senderUsername=" + senderUsername +
                ", receiverUsername=" + receiverUsername +
                ", amount=" + amount +
                '}';
    }
}
