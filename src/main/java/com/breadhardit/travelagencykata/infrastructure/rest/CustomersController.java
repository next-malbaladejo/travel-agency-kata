package com.breadhardit.travelagencykata.infrastructure.rest;

import com.breadhardit.travelagencykata.application.command.command.CreateCustomerCommand;
import com.breadhardit.travelagencykata.application.command.query.GetCustomerByIdQuery;
import com.breadhardit.travelagencykata.application.command.query.GetCustomerByPassportQuery;
import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.rest.dto.GetCustomerDTO;
import com.breadhardit.travelagencykata.infrastructure.rest.dto.PutCustomerDTO;
import com.breadhardit.travelagencykata.infrastructure.rest.mapper.CustomerRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CustomersController {

    private final CustomersRepository customersRepository;
    private final CustomerRestMapper customerRestMapper;

    @PutMapping("/customers")
    @Transactional
    public ResponseEntity<Void> putCustomer(@RequestBody PutCustomerDTO customer) {
        log.info("PUT customer {}", customer);
        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .passportNumber(customer.getPassportNumber())
                .customersRepository(customersRepository)
                .build();
        String id = command.handle();
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{customer-id}")
                        .buildAndExpand(id)
                        .toUri()).build();
    }

    @GetMapping("/customers/{customer-id}")
    public ResponseEntity<GetCustomerDTO> getCustomer(@PathVariable("customer-id") String customerId) {
        log.info("Getting the customer {}", customerId);
        Optional<Customer> customer = GetCustomerByIdQuery.builder()
                .customersRepository(customersRepository)
                .id(customerId)
                .build().handle();
        return customer.map(value -> ResponseEntity.ok(customerRestMapper.toGetCustomerDTO(value)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<GetCustomerDTO>> getCustomers(@RequestParam(name = "passport-number") String passportNumber) {
        log.info("Getting the customer with the passport {}", passportNumber);
        Optional<Customer> customer = GetCustomerByPassportQuery.builder()
                .customersRepository(customersRepository)
                .passportNumber(passportNumber)
                .build().handle();
        return customer.map(value -> ResponseEntity.ok(List.of(customerRestMapper.toGetCustomerDTO(value))))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}