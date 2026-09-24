package kz.bank.service;

import kz.bank.model.Transaction;
import kz.bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(
            Long id,
            Long accountId,
            BigDecimal amount
    ) {

        Transaction transaction = new Transaction(
                id,
                accountId,
                amount
        );

        transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}  