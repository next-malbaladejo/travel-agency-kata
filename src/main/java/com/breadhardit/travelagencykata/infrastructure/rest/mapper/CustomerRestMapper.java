package com.breadhardit.travelagencykata.infrastructure.rest.mapper;

import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.rest.dto.GetCustomerDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerRestMapper {

    public GetCustomerDTO toGetCustomerDTO(Customer customer) {
        return GetCustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .passportNumber(customer.getPassportNumber())
                .build();
    }
}