package com.breadhardit.travelagencykata.application.command.query;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class GetCustomerByIdQuery {
    String id;
    CustomersRepository customersRepository;

    public Optional<Customer> handle() {
        return customersRepository.getCustomerById(id);
    }
}