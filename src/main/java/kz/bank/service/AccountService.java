package kz.bank.service;

import kz.bank.model.Account;
import kz.bank.model.DepositType;
import kz.bank.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void createAccount(
            Long id,
            String accountNumber,
            DepositType depositType,
            Long customerId
    ) {

        Account account = new Account(
                id,
                accountNumber,
                depositType,
                customerId
        );

        accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public void deposit(Long accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        account.setBalance(
                account.getBalance().add(amount)
        );
    }

    public void withdraw(Long accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        if (account.getDepositType() == DepositType.NO_WITHDRAW) {
            System.out.println("С этого вклада нельзя снимать деньги.");
            return;
        }

        if (account.getBalance().compareTo(amount) < 0) {
            System.out.println("Недостаточно денег.");
            return;
        }

        account.setBalance(
                account.getBalance().subtract(amount)
        );
    }

    public void transfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount
    ) {

        Account fromAccount = accountRepository.findById(fromAccountId);
        Account toAccount = accountRepository.findById(toAccountId);

        if (fromAccount == null || toAccount == null) {
            System.out.println("Один из счетов не найден.");
            return;
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            System.out.println("Недостаточно денег для перевода.");
            return;
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(amount)
        );

        toAccount.setBalance(
                toAccount.getBalance().add(amount)
        );
    }
} 