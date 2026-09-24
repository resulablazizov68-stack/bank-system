package kz.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String type;

    public Transaction(
            Long id,
            Long accountId,
            BigDecimal amount,
            String type
    ) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getType() {
        return type;
    }
} 