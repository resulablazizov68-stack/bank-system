package kz.bank.service;

import kz.bank.model.Account;
import kz.bank.model.Loan;
import kz.bank.model.LoanStatus;
import kz.bank.repository.LoanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

    // Максимальная сумма кредита
    private static final BigDecimal MAX_LOAN_AMOUNT =
            new BigDecimal("1000000");

    // Процентная ставка
    private static final BigDecimal LOAN_RATE =
            new BigDecimal("30");

    public LoanService(
            LoanRepository loanRepository,
            AccountService accountService,
            TransactionService transactionService
    ) {
        this.loanRepository = loanRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    // Создание кредита
    public void createLoan(
            Long id,
            Long customerId,
            Long accountId,
            BigDecimal amount,
            int termMonths
    ) {

        // Проверяем максимальную сумму
        if (amount.compareTo(MAX_LOAN_AMOUNT) > 0) {
            System.out.println(
                    "Банк не может выдать больше 1 000 000 ₸."
            );
            return;
        }

        // Проверяем положительную сумму
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println(
                    "Сумма кредита должна быть больше 0."
            );
            return;
        }

        // Проверяем срок
        if (termMonths <= 0) {
            System.out.println(
                    "Срок кредита должен быть больше 0."
            );
            return;
        }

        // Проверяем существование счёта
        Account account =
                accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Счёт не найден.");
            return;
        }

        // Проверяем блокировку счёта
        if (account.isBlocked()) {
            System.out.println(
                    "Этот счёт заблокирован."
            );
            return;
        }

        // Проверяем владельца счёта
        if (!account.getCustomerId().equals(customerId)) {
            System.out.println(
                    "Этот счёт не принадлежит указанному клиенту."
            );
            return;
        }

        /*
         * Формула аннуитетного платежа:
         *
         * P = S × i × (1+i)^n / ((1+i)^n - 1)
         *
         * S — сумма кредита
         * i — месячная процентная ставка
         * n — количество месяцев
         */

        BigDecimal monthlyRate =
                LOAN_RATE
                        .divide(
                                new BigDecimal("100"),
                                10,
                                RoundingMode.HALF_UP
                        )
                        .divide(
                                new BigDecimal("12"),
                                10,
                                RoundingMode.HALF_UP
                        );

        double i = monthlyRate.doubleValue();
        double s = amount.doubleValue();
        int n = termMonths;

        double payment =
                s * i * Math.pow(1 + i, n)
                        / (Math.pow(1 + i, n) - 1);

        BigDecimal monthlyPayment =
                BigDecimal.valueOf(payment)
                        .setScale(2, RoundingMode.HALF_UP);

        // Создаём кредит
        Loan loan = new Loan(
                id,
                customerId,
                amount,
                LOAN_RATE,
                termMonths,
                monthlyPayment
        );

        loanRepository.save(loan);

        // Выдаём сумму кредита на счёт клиента
        account.setBalance(
                account.getBalance().add(amount)
        );

        // Транзакция выдачи кредита
        transactionService.createTransaction(
                accountId,
                amount,
                "LOAN_ISSUED"
        );

        System.out.println();
        System.out.println("Кредит одобрен.");
        System.out.println("Сумма: " + amount + " ₸");
        System.out.println("Ставка: " + LOAN_RATE + "%");
        System.out.println("Срок: " + termMonths + " мес.");
        System.out.println(
                "Ежемесячный платёж: "
                        + monthlyPayment
                        + " ₸"
        );
    }

    // Получить все кредиты
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Найти кредит
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    // Симуляция одного месяца
    public void simulateMonth(Long loanId) {

        Loan loan =
                loanRepository.findById(loanId);

        if (loan == null) {
            System.out.println("Кредит не найден.");
            return;
        }

        if (loan.getStatus() == LoanStatus.PAID) {
            System.out.println(
                    "Кредит уже полностью погашен."
            );
            return;
        }

        if (loan.getStatus() == LoanStatus.BLOCKED) {
            System.out.println(
                    "Кредит заблокирован."
            );
            return;
        }

        Account account =
                findCustomerAccount(
                        loan.getCustomerId()
                );

        if (account == null) {
            System.out.println(
                    "Счёт клиента не найден."
            );
            return;
        }

        // Проверяем блокировку счёта
        if (account.isBlocked()) {
            System.out.println(
                    "Счёт клиента заблокирован."
            );
            return;
        }

        BigDecimal payment =
                loan.getMonthlyPayment();

        // Если денег хватает
        if (account.getBalance().compareTo(payment) >= 0) {

            account.setBalance(
                    account.getBalance().subtract(payment)
            );

            loan.setPaidAmount(
                    loan.getPaidAmount().add(payment)
            );

            loan.setRemainingAmount(
                    loan.getRemainingAmount().subtract(payment)
            );

            loan.setSimulatedDate(
                    loan.getSimulatedDate().plusMonths(1)
            );

            transactionService.createTransaction(
                    account.getId(),
                    payment.negate(),
                    "LOAN_PAYMENT"
            );

            // Проверяем полное погашение
            if (loan.getRemainingAmount()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                loan.setRemainingAmount(
                        BigDecimal.ZERO
                );

                loan.setStatus(
                        LoanStatus.PAID
                );

                System.out.println(
                        "Кредит полностью погашен."
                );

            } else {

                System.out.println(
                        "Платёж успешно внесён: "
                                + payment
                                + " ₸"
                );
            }

        } else {

            // Денег недостаточно
            loan.setStatus(
                    LoanStatus.OVERDUE
            );

            loan.setSimulatedDate(
                    loan.getSimulatedDate().plusMonths(1)
            );

            System.out.println(
                    "Недостаточно денег для платежа."
            );

            System.out.println(
                    "Кредит перешёл в просрочку."
            );

            // Начинаем списание со всех счетов клиента
            collectFromDeposits(loan);
        }
    }

    // Поиск счёта клиента
    private Account findCustomerAccount(
            Long customerId
    ) {

        List<Account> accounts =
                accountService.getAllAccounts();

        for (Account account : accounts) {

            if (account.getCustomerId()
                    .equals(customerId)
                    && !account.isBlocked()) {

                return account;
            }
        }

        return null;
    }

    // Использование средств клиента
    // для погашения кредита
    private void collectFromDeposits(Loan loan) {

        List<Account> accounts =
                accountService.getAllAccounts();

        BigDecimal debt =
                loan.getRemainingAmount();

        for (Account account : accounts) {

            // Берём только счета этого клиента
            if (!account.getCustomerId()
                    .equals(loan.getCustomerId())) {

                continue;
            }

            // Если долг уже погашен
            if (debt.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal balance =
                    account.getBalance();

            // Пустой счёт пропускаем
            if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal taken;

            // Сколько можно списать
            if (balance.compareTo(debt) >= 0) {

                taken = debt;

            } else {

                taken = balance;
            }

            // Списываем деньги
            account.setBalance(
                    balance.subtract(taken)
            );

            // Уменьшаем долг
            debt = debt.subtract(taken);

            // Создаём транзакцию
            transactionService.createTransaction(
                    account.getId(),
                    taken.negate(),
                    "LOAN_COLLECTION"
            );

            System.out.println(
                    "Списано со счёта "
                            + account.getAccountNumber()
                            + ": "
                            + taken
                            + " ₸"
            );
        }

        // Сохраняем новый остаток долга
        loan.setRemainingAmount(debt);

        // Если долг погашен
        if (debt.compareTo(BigDecimal.ZERO) <= 0) {

            loan.setRemainingAmount(
                    BigDecimal.ZERO
            );

            loan.setStatus(
                    LoanStatus.PAID
            );

            System.out.println(
                    "Кредит полностью погашен "
                            + "за счёт средств клиента."
            );

        } else {

            // Денег клиента не хватило
            loan.setStatus(
                    LoanStatus.BLOCKED
            );

            // Блокируем ВСЕ счета клиента
            for (Account account : accounts) {

                if (account.getCustomerId()
                        .equals(loan.getCustomerId())) {

                    account.setBlocked(true);
                }
            }

            System.out.println(
                    "Средств клиента недостаточно."
            );

            System.out.println(
                    "Все счета клиента заблокированы."
            );

            System.out.println(
                    "Остаток задолженности: "
                            + debt
                            + " ₸"
            );
        }
    }
} 