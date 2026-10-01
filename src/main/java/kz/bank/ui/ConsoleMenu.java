package kz.bank.ui;

import kz.bank.model.Customer;
import kz.bank.model.Account;
import kz.bank.model.Transaction;
import kz.bank.model.DepositType;
import kz.bank.model.Loan;
import kz.bank.service.CustomerService;
import kz.bank.service.AccountService;
import kz.bank.service.TransactionService;
import kz.bank.service.LoanService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;

    public ConsoleMenu(
            CustomerService customerService,
            AccountService accountService,
            TransactionService transactionService,
            LoanService loanService
    ) {
        this.customerService = customerService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.loanService = loanService;
    }

    public void start() {

        while (true) {

            System.out.println();
            System.out.println("========== БАНК ==========");
            System.out.println("1. Создать клиента");
            System.out.println("2. Показать клиентов");
            System.out.println("3. Создать счёт");
            System.out.println("4. Показать счета");
            System.out.println("5. Пополнить счёт");
            System.out.println("6. Снять деньги");
            System.out.println("7. Перевести деньги");
            System.out.println("8. Показать транзакции");
            System.out.println("9. Симулировать месяц");
            System.out.println("10. Досрочно закрыть депозит");
            System.out.println("11. Создать кредит");
            System.out.println("12. Показать кредиты");
            System.out.println("13. Симулировать погашение кредита");
            System.out.println("0. Выход");
            System.out.println("==========================");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    createCustomer();
                    break;

                case 2:
                    showCustomers();
                    break;

                case 3:
                    createAccount();
                    break;

                case 4:
                    showAccounts();
                    break;

                case 5:
                    deposit();
                    break;

                case 6:
                    withdraw();
                    break;

                case 7:
                    transfer();
                    break;

                case 8:
                    showTransactions();
                    break;

                case 9:
                    simulateMonth();
                    break;

                case 10:
                    closeEarly();
                    break;

                case 11:
                    createLoan();
                    break;

                case 12:
                    showLoans();
                    break;

                case 13:
                    simulateLoanMonth();
                    break;

                case 0:
                    System.out.println("Программа завершена.");
                    return;

                default:
                    System.out.println("Неверный пункт меню.");
            }
        }
    }

    private void createCustomer() {

        System.out.print("ID клиента: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Имя: ");
        String firstName = scanner.nextLine();

        System.out.print("Фамилия: ");
        String lastName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        customerService.createCustomer(
                id,
                firstName,
                lastName,
                email
        );

        System.out.println("Клиент создан.");
    }

    private void showCustomers() {

        List<Customer> customers =
                customerService.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("Клиентов пока нет.");
            return;
        }

        for (Customer customer : customers) {

            System.out.println(
                    "ID: " + customer.getId()
                            + " | Имя: " + customer.getFirstName()
                            + " | Фамилия: " + customer.getLastName()
                            + " | Email: " + customer.getEmail()
            );
        }
    }

    private void createAccount() {

        System.out.print("ID счёта: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Номер счёта: ");
        String accountNumber = scanner.nextLine();

        System.out.println("Тип вклада:");
        System.out.println("1. Можно снимать");
        System.out.println("2. Нельзя снимать");
        System.out.print("Выберите: ");

        int type = scanner.nextInt();

        DepositType depositType;

        if (type == 1) {
            depositType = DepositType.WITHDRAW_ALLOWED;
        } else {
            depositType = DepositType.NO_WITHDRAW;
        }

        System.out.print("ID клиента: ");
        Long customerId = scanner.nextLong();

        System.out.print("Процентная ставка (например 17.5): ");
        BigDecimal rate = scanner.nextBigDecimal();

        System.out.println("Срок депозита:");
        System.out.println("3 - 3 месяца");
        System.out.println("6 - 6 месяцев");
        System.out.println("9 - 9 месяцев");
        System.out.println("12 - 12 месяцев");
        System.out.print("Введите срок: ");

        int termMonths = scanner.nextInt();

        accountService.createAccount(
                id,
                accountNumber,
                depositType,
                customerId,
                rate,
                termMonths
        );

        System.out.println("Счёт создан.");
    }

    private void showAccounts() {

        List<Account> accounts =
                accountService.getAllAccounts();

        if (accounts.isEmpty()) {
            System.out.println("Счетов пока нет.");
            return;
        }

        for (Account account : accounts) {

            System.out.println(
                    "ID: " + account.getId()
                            + " | Номер: " + account.getAccountNumber()
                            + " | Баланс: " + account.getBalance()
                            + " | Тип: " + account.getDepositType()
                            + " | Клиент ID: " + account.getCustomerId()
                            + " | Ставка: " + account.getRate() + "%"
                            + " | Срок: " + account.getTermMonths() + " мес."
                            + " | Открытие: " + account.getOpenDate()
                            + " | Окончание: " + account.getEndDate()
                            + " | Симуляция: " + account.getSimulatedDate()
                            + " | Вознаграждение: " + account.getAccruedReward()
                            + " | Закрыт: " + account.isClosed()
                            + " | Заблокирован: " + account.isBlocked()
            );
        }
    }

    private void deposit() {

        System.out.print("ID счёта: ");
        Long accountId = scanner.nextLong();

        System.out.print("Сумма пополнения: ");
        BigDecimal amount = scanner.nextBigDecimal();

        accountService.deposit(accountId, amount);
    }

    private void withdraw() {

        System.out.print("ID счёта: ");
        Long accountId = scanner.nextLong();

        System.out.print("Сумма снятия: ");
        BigDecimal amount = scanner.nextBigDecimal();

        accountService.withdraw(accountId, amount);
    }

    private void transfer() {

        System.out.print("ID счёта отправителя: ");
        Long fromAccountId = scanner.nextLong();

        System.out.print("ID счёта получателя: ");
        Long toAccountId = scanner.nextLong();

        System.out.print("Сумма перевода: ");
        BigDecimal amount = scanner.nextBigDecimal();

        accountService.transfer(
                fromAccountId,
                toAccountId,
                amount
        );
    }

    private void showTransactions() {

        List<Transaction> transactions =
                transactionService.getAllTransactions();

        if (transactions.isEmpty()) {
            System.out.println("Транзакций пока нет.");
            return;
        }

        for (Transaction transaction : transactions) {

            System.out.println(
                    "ID: " + transaction.getId()
                            + " | Счёт: " + transaction.getAccountId()
                            + " | Сумма: " + transaction.getAmount()
                            + " | Тип: " + transaction.getType()
                            + " | Время: " + transaction.getCreatedAt()
            );
        }
    }

    private void simulateMonth() {

        System.out.print("ID счёта: ");
        Long accountId = scanner.nextLong();

        accountService.simulateMonth(accountId);
    }

    private void closeEarly() {

        System.out.print("ID счёта: ");
        Long accountId = scanner.nextLong();

        accountService.closeEarly(accountId);
    }

    // =========================
    // КРЕДИТЫ
    // =========================

    private void createLoan() {

        System.out.println();
        System.out.println("===== СОЗДАНИЕ КРЕДИТА =====");

        System.out.print("ID кредита: ");
        Long id = scanner.nextLong();

        System.out.print("ID клиента: ");
        Long customerId = scanner.nextLong();

        System.out.print("ID счёта: ");
        Long accountId = scanner.nextLong();

        System.out.print("Сумма кредита: ");
        BigDecimal amount = scanner.nextBigDecimal();

        System.out.print("Срок кредита в месяцах: ");
        int termMonths = scanner.nextInt();

        loanService.createLoan(
                id,
                customerId,
                accountId,
                amount,
                termMonths
        );
    }

    private void showLoans() {

        List<Loan> loans =
                loanService.getAllLoans();

        if (loans.isEmpty()) {
            System.out.println("Кредитов пока нет.");
            return;
        }

        System.out.println();
        System.out.println("========== КРЕДИТЫ ==========");

        for (Loan loan : loans) {

            System.out.println();

            System.out.println(
                    "ID кредита: "
                            + loan.getId()
            );

            System.out.println(
                    "Клиент ID: "
                            + loan.getCustomerId()
            );

            System.out.println(
                    "Сумма кредита: "
                            + loan.getAmount()
                            + " ₸"
            );

            System.out.println(
                    "Ставка: "
                            + loan.getRate()
                            + "%"
            );

            System.out.println(
                    "Срок: "
                            + loan.getTermMonths()
                            + " мес."
            );

            System.out.println(
                    "Ежемесячный платёж: "
                            + loan.getMonthlyPayment()
                            + " ₸"
            );

            System.out.println(
                    "Оплачено: "
                            + loan.getPaidAmount()
                            + " ₸"
            );

            System.out.println(
                    "Остаток задолженности: "
                            + loan.getRemainingAmount()
                            + " ₸"
            );

            System.out.println(
                    "Дата начала: "
                            + loan.getStartDate()
            );

            System.out.println(
                    "Дата окончания: "
                            + loan.getEndDate()
            );

            System.out.println(
                    "Дата симуляции: "
                            + loan.getSimulatedDate()
            );

            System.out.println(
                    "Статус: "
                            + loan.getStatus()
            );

            System.out.println(
                    "----------------------------"
            );
        }
    }

    private void simulateLoanMonth() {

        System.out.println();
        System.out.println(
                "===== ПОГАШЕНИЕ КРЕДИТА ====="
        );

        System.out.print("ID кредита: ");
        Long loanId = scanner.nextLong();

        loanService.simulateMonth(loanId);
    }
}  