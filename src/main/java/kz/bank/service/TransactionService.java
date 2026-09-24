package kz.bank.service;

import kz.bank.model.Transaction;
import kz.bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

public class TransactionService {

    private final TransactionRepository transactionRepository;

    // Следующий ID транзакции
    private Long nextTransactionId = 1L;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(
            Long accountId, 
            BigDecimal amount,
            String type
    ) {

        // Автоматически создаём уникальный ID
        Long id = nextTransactionId++;

        Transaction transaction = new Transaction(
                id,
                accountId,
                amount,
                type
        );

        transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}