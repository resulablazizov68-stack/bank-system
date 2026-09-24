package kz.bank.model;

import java.math.BigDecimal;

public class Account {

    private Long id;
    private String accountNumber;
    private DepositType depositType;
    private BigDecimal balance;
    private Long customerId;

    public Account(
            Long id,
            String accountNumber,
            DepositType depositType,
            Long customerId
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.depositType = depositType;
        this.customerId = customerId;
        this.balance = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public DepositType getDepositType() {
        return depositType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}  