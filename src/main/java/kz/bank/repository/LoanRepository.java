package kz.bank.repository;

import kz.bank.model.Loan;

import java.util.ArrayList;
import java.util.List;

public class LoanRepository {

    private final List<Loan> loans = new ArrayList<>();

    public void save(Loan loan) {
        loans.add(loan);
    }

    public List<Loan> findAll() {
        return loans;
    }

    public Loan findById(Long id) {

        for (Loan loan : loans) {

            if (loan.getId().equals(id)) {
                return loan;
            }
        }

        return null;
    }
} 