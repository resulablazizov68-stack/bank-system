package kz.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Account {

    private Long id;
    private String accountNumber;
    private DepositType depositType;
    private BigDecimal balance;
    private Long customerId;

    // Процентная ставка
    private BigDecimal rate;

    // Срок депозита в месяцах
    private int termMonths;

    // Дата открытия депозита
    private LocalDate openDate;

    // Дата окончания депозита
    private LocalDate endDate;

    // Дата, до которой была проведена симуляция
    private LocalDate simulatedDate;

    // Накопленное вознаграждение
    private BigDecimal accruedReward;

    // Закрыт ли депозит
    private boolean closed;

    public Account(
            Long id,
            String accountNumber,
            DepositType depositType,
            Long customerId,
            BigDecimal rate,
            int termMonths
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.depositType = depositType;
        this.customerId = customerId;

        this.balance = BigDecimal.ZERO;

        this.rate = rate;
        this.termMonths = termMonths;

        // Дата открытия — сегодня
        this.openDate = LocalDate.now();

        // Дата окончания рассчитывается автоматически
        this.endDate = openDate.plusMonths(termMonths);

        // В начале симуляция находится на дате открытия
        this.simulatedDate = openDate;

        // Пока вознаграждение не начислено
        this.accruedReward = BigDecimal.ZERO;

        this.closed = false;
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

    public BigDecimal getRate() {
        return rate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getSimulatedDate() {
        return simulatedDate;
    }

    public BigDecimal getAccruedReward() {
        return accruedReward;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setSimulatedDate(LocalDate simulatedDate) {
        this.simulatedDate = simulatedDate;
    }

    public void setAccruedReward(BigDecimal accruedReward) {
        this.accruedReward = accruedReward;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}   