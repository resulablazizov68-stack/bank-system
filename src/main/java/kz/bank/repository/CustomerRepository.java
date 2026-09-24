package kz.bank.repository;

import kz.bank.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    private final List<Customer> customers = new ArrayList<>();

    public void save(Customer customer) {
        customers.add(customer);
    }

    public List<Customer> findAll() {
        return customers;
    }

    public Customer findById(Long id) {

        for (Customer customer : customers) {

            if (customer.getId().equals(id)) {
                return customer;
            }
        }

        return null;
    }
}  