
package com.breadhardit.travelagencykata.infrastructure.persistence.repository;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import org.springframework.stereotype.Repository;
import com.breadhardit.travelagencykata.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.context.annotation.Primary;
import java.util.Optional;
import java.time.LocalDate;

@Repository
@Primary
public class CustomersRepositoryAdapter implements CustomersRepository {

    private final CustomersJPARepository jpaRepository;

    public CustomersRepositoryAdapter(CustomersJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public void saveCustomer(Customer customer) {

        CustomerEntity entity = CustomerEntity.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .passportNumber(customer.getPassportNumber())
                .enrollmentDate(
                        customer.getEnrollmentDate() != null
                                ? customer.getEnrollmentDate()
                                : LocalDate.now()
                )
                .active(
                        customer.getActive() != null
                                ? customer.getActive()
                                : true
                )
                .build();

        jpaRepository.save(entity);
    }

    @Override
    public Optional<Customer> getCustomerById(String id) {
        return jpaRepository.findById(id)
                .map(this::toCustomer);
    }


    private Customer toCustomer(CustomerEntity entity) {
        return Customer.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surnames(entity.getSurnames())
                .birthDate(entity.getBirthDate())
                .passportNumber(entity.getPassportNumber())
                .enrollmentDate(entity.getEnrollmentDate())
                .active(entity.getActive())
                .build();
    }



    @Override
    public Optional<Customer> getCustomerByPassport(String id) {
        return jpaRepository.findAll()
                .stream()
                .filter(entity -> id.equals(entity.getPassportNumber()))
                .findFirst()
                .map(this::toCustomer);
    }

}
