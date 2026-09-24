package kz.bank.service;

import kz.bank.model.Customer;
import kz.bank.repository.CustomerRepository;

import java.util.List;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void createCustomer(
            Long id,
            String firstName,
            String lastName,
            String email
    ) {

        Customer customer = new Customer(
                id,
                firstName,
                lastName,
                email
        );

        customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
}  