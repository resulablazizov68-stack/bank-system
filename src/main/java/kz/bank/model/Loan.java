package kz.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Loan { 

    private Long id;

    // ID клиента, которому выдан кредит
    private Long customerId;

    // Сумма кредита
    private BigDecimal amount;

    // Процентная ставка
    private BigDecimal rate;

    // Срок кредита в месяцах
    private int termMonths;

    // Ежемесячный платёж
    private BigDecimal monthlyPayment;

    // Сколько уже погашено
    private BigDecimal paidAmount;

    // Остаток долга
    private BigDecimal remainingAmount;

    // Дата выдачи
    private LocalDate startDate;

    // Дата окончания
    private LocalDate endDate;

    // Текущая дата симуляции
    private LocalDate simulatedDate;

    // Статус кредита
    private LoanStatus status;

    public Loan(
            Long id,
            Long customerId,
            BigDecimal amount,
            BigDecimal rate,
            int termMonths,
            BigDecimal monthlyPayment
    ) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.rate = rate;
        this.termMonths = termMonths;
        this.monthlyPayment = monthlyPayment;

        this.paidAmount = BigDecimal.ZERO;

        // Изначально весь кредит является долгом
        this.remainingAmount = monthlyPayment
                .multiply(BigDecimal.valueOf(termMonths));

        this.startDate = LocalDate.now();

        this.endDate = startDate.plusMonths(termMonths);

        this.simulatedDate = startDate;

        this.status = LoanStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getSimulatedDate() {
        return simulatedDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public void setSimulatedDate(LocalDate simulatedDate) {
        this.simulatedDate = simulatedDate;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}