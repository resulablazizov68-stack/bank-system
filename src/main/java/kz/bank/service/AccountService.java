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

    public void deposit(
            Long accountId,
            BigDecimal amount
    ) {

        Account account =
                accountRepository.findById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        // Проверяем блокировку
        if (account.isBlocked()) {
            System.out.println(
                    "Счёт заблокирован. "
                            + "Пополнение невозможно."
            );
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println(
                    "Сумма должна быть больше нуля."
            );
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

        System.out.println(
                "Счёт пополнен на "
                        + amount
                        + " ₸."
        );
    }

    public void withdraw(
            Long accountId,
            BigDecimal amount
    ) {

        Account account =
                accountRepository.findById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        // Проверяем блокировку
        if (account.isBlocked()) {
            System.out.println(
                    "Счёт заблокирован. "
                            + "Снятие невозможно."
            );
            return;
        }

        if (account.getDepositType()
                == DepositType.NO_WITHDRAW) {

            System.out.println(
                    "С этого вклада нельзя снимать деньги."
            );
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println(
                    "Сумма должна быть больше нуля."
            );
            return;
        }

        if (account.getBalance()
                .compareTo(amount) < 0) {

            System.out.println(
                    "Недостаточно денег."
            );
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

        System.out.println(
                "Снято: "
                        + amount
                        + " ₸."
        );
    }

    public void transfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount
    ) {

        Account fromAccount =
                accountRepository.findById(
                        fromAccountId
                );

        Account toAccount =
                accountRepository.findById(
                        toAccountId
                );

        if (fromAccount == null
                || toAccount == null) {

            System.out.println(
                    "Один из счетов не найден."
            );
            return;
        }

        // Проверяем блокировку отправителя
        if (fromAccount.isBlocked()) {

            System.out.println(
                    "Счёт отправителя заблокирован."
            );
            return;
        }

        // Проверяем блокировку получателя
        if (toAccount.isBlocked()) {

            System.out.println(
                    "Счёт получателя заблокирован."
            );
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            System.out.println(
                    "Сумма перевода должна быть "
                            + "больше нуля."
            );
            return;
        }

        if (fromAccount.getBalance()
                .compareTo(amount) < 0) {

            System.out.println(
                    "Недостаточно денег для перевода."
            );
            return;
        }

        fromAccount.setBalance(
                fromAccount.getBalance()
                        .subtract(amount)
        );

        toAccount.setBalance(
                toAccount.getBalance()
                        .add(amount)
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

        System.out.println(
                "Перевод выполнен: "
                        + amount
                        + " ₸."
        );
    }

    public void simulateMonth(Long accountId) {

        Account account =
                accountRepository.findById(accountId);

        if (account == null) {
            System.out.println(
                    "Счёт не найден."
            );
            return;
        }

        if (account.isBlocked()) {
            System.out.println(
                    "Счёт заблокирован. "
                            + "Симуляция невозможна."
            );
            return;
        }

        if (account.isClosed()) {
            System.out.println(
                    "Депозит уже закрыт."
            );
            return;
        }

        if (!account.getSimulatedDate()
                .isBefore(account.getEndDate())) {

            System.out.println(
                    "Срок депозита уже закончился."
            );
            return;
        }

        // Переводим симуляцию на месяц вперёд
        LocalDate newDate =
                account.getSimulatedDate()
                        .plusMonths(1);

        // Месячное вознаграждение
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

        // Добавляем вознаграждение
        account.setBalance(
                account.getBalance().add(reward)
        );

        // Сохраняем начисленное вознаграждение
        account.setAccruedReward(
                account.getAccruedReward()
                        .add(reward)
        );

        // Обновляем дату
        account.setSimulatedDate(newDate);

        // Создаём транзакцию
        transactionService.createTransaction(
                accountId,
                reward,
                "REWARD"
        );

        System.out.println(
                "Месяц смоделирован."
        );

        System.out.println(
                "Начислено: "
                        + reward
                        + " ₸."
        );
    }

    public void closeEarly(Long accountId) {

        Account account =
                accountRepository.findById(accountId);

        if (account == null) {
            System.out.println(
                    "Счёт не найден."
            );
            return;
        }

        if (account.isBlocked()) {
            System.out.println(
                    "Счёт заблокирован."
            );
            return;
        }

        if (account.isClosed()) {
            System.out.println(
                    "Депозит уже закрыт."
            );
            return;
        }

        if (!account.getSimulatedDate()
                .isBefore(account.getEndDate())) {

            System.out.println(
                    "Срок депозита уже закончился."
            );
            return;
        }

        // Убираем начисленное вознаграждение
        account.setBalance(
                account.getBalance()
                        .subtract(
                                account.getAccruedReward()
                        )
        );

        account.setAccruedReward(
                BigDecimal.ZERO
        );

        account.setClosed(true);

        System.out.println(
                "Депозит закрыт досрочно."
        );

        System.out.println(
                "Начисленное вознаграждение "
                        + "аннулировано."
        );
    }
}   