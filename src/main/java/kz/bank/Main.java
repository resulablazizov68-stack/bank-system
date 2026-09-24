package kz.bank;

import kz.bank.repository.AccountRepository;
import kz.bank.repository.CustomerRepository;
import kz.bank.repository.TransactionRepository;
import kz.bank.service.AccountService;
import kz.bank.service.CustomerService;
import kz.bank.service.TransactionService;
import kz.bank.ui.ConsoleMenu;

public class Main {

    public static void main(String[] args) {

        // Репозитории
        CustomerRepository customerRepository =
                new CustomerRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

        // Сервисы
        CustomerService customerService =
                new CustomerService(customerRepository);

        TransactionService transactionService =
                new TransactionService(transactionRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        transactionService
                );

        // Консольное меню
        ConsoleMenu consoleMenu =
                new ConsoleMenu(
                        customerService,
                        accountService,
                        transactionService
                );

        consoleMenu.start();
    }
} 