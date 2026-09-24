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

        CustomerRepository customerRepository =
                new CustomerRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

        CustomerService customerService =
                new CustomerService(customerRepository);

        AccountService accountService =
                new AccountService(accountRepository);

        TransactionService transactionService =
                new TransactionService(transactionRepository);

        ConsoleMenu consoleMenu =
                new ConsoleMenu(
                        customerService,
                        accountService,
                        transactionService
                );

        consoleMenu.start();
    }
} 