package kz.bank.repository;

import kz.bank.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    private final List<Account> accounts = new ArrayList<>();

    public void save(Account account) {
        accounts.add(account);
    }

    public List<Account> findAll() {
        return accounts;
    }

    public Account findById(Long id) {

        for (Account account : accounts) {

            if (account.getId().equals(id)) {
                return account;
            }
        }

        return null;
    }
}  