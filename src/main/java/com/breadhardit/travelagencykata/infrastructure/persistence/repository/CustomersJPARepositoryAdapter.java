package com.breadhardit.travelagencykata.infrastructure.persistence.repository;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.persistence.mapper.CustomerPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomersJPARepositoryAdapter implements CustomersRepository {

    private final CustomersJPARepository customersJPARepository;
    private final CustomerPersistenceMapper mapper;

    @Override
    public void saveCustomer(Customer customer) {
        customersJPARepository.save(mapper.toEntity(customer));
    }

    @Override
    public Optional<Customer> getCustomerById(String id) {
        return customersJPARepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> getCustomerByPassport(String passportNumber) {
        return customersJPARepository.findByPassportNumber(passportNumber)
                .map(mapper::toDomain);
    }
}