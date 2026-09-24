package kz.bank.service;

import kz.bank.model.Account;
import kz.bank.model.DepositType;
import kz.bank.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public AccountService(
            AccountRepository accountRepository,
            TransactionService transactionService
    ) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    public void createAccount(
            Long id,
            String accountNumber,
            DepositType depositType,
            Long customerId,
            BigDecimal rate,
            int termMonths
    ) {

        Account account = new Account(
                id,
                accountNumber,
                depositType,
                customerId,
                rate,
                termMonths
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

        transactionService.createTransaction(
                accountId,
                amount,
                "DEPOSIT"
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

        transactionService.createTransaction(
                accountId,
                amount.negate(),
                "WITHDRAW"
        );
    }

    public void transfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount
    ) {

        Account fromAccount =
                accountRepository.findById(fromAccountId);

        Account toAccount =
                accountRepository.findById(toAccountId);

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

        transactionService.createTransaction(
                fromAccountId,
                amount.negate(),
                "TRANSFER_OUT"
        );

        transactionService.createTransaction(
                toAccountId,
                amount,
                "TRANSFER_IN"
        );
    }

    public void simulateMonth(Long accountId) {

        Account account = accountRepository.findById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        if (account.isClosed()) {
            System.out.println("Депозит уже закрыт.");
            return;
        }

        if (!account.getSimulatedDate().isBefore(account.getEndDate())) {
            System.out.println("Срок депозита уже закончился.");
            return;
        }

        // Переводим симуляцию на один месяц вперёд
        LocalDate newDate =
                account.getSimulatedDate().plusMonths(1);

        // Месячное вознаграждение:
        // баланс × годовая ставка / 100 / 12
        BigDecimal reward =
                account.getBalance()
                        .multiply(account.getRate())
                        .divide(
                                new BigDecimal("100"),
                                10,
                                RoundingMode.HALF_UP
                        )
                        .divide(
                                new BigDecimal("12"),
                                2,
                                RoundingMode.HALF_UP
                        );

        // Добавляем вознаграждение к балансу
        account.setBalance(
                account.getBalance().add(reward)
        );

        // Сохраняем общую сумму начисленного вознаграждения
        account.setAccruedReward(
                account.getAccruedReward().add(reward)
        );

        // Обновляем дату симуляции
        account.setSimulatedDate(newDate);

        // Создаём транзакцию вознаграждения
        transactionService.createTransaction(
                accountId,
                reward,
                "REWARD"
        );

        System.out.println(
                "Месяц смоделирован. Начислено: "
                        + reward
        );
    }

    public void closeEarly(Long accountId) {

        Account account = accountRepository.findById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        if (account.isClosed()) {
            System.out.println("Депозит уже закрыт.");
            return;
        }

        if (!account.getSimulatedDate().isBefore(account.getEndDate())) {
            System.out.println("Срок депозита уже закончился.");
            return;
        }

        // Убираем всё ранее начисленное вознаграждение
        account.setBalance(
                account.getBalance()
                        .subtract(account.getAccruedReward())
        );

        // Обнуляем накопленное вознаграждение
        account.setAccruedReward(
                BigDecimal.ZERO
        );

        // Закрываем депозит
        account.setClosed(true);

        System.out.println(
                "Депозит закрыт досрочно."
        );
        System.out.println(
                "Начисленное вознаграждение аннулировано."
        );
    }
} 