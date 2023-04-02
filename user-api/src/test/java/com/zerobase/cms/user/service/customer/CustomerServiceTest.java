package com.zerobase.cms.user.service.customer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void findByIdAndEmail() {
        given(customerRepository.findById(anyLong()).stream().filter(
            customer -> customer.getEmail().equals(anyString())).findFirst())
            .willReturn(Optional.of(Customer.builder()
                    .email("zerobase@naver.com")
                    .name("zerobase")
                    .password("1234")
                .build()));

        Customer customer = customerService.findByIdAndEmail(1L, "zerobase@naver.com").get();

        assertEquals("zerobase@naver.com", customer.getEmail());
        assertEquals("zerobase", customer.getName());
        assertEquals("1234", customer.getPassword());
    }

    @Test
    void findValidCustomer() {
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Customer.builder()
                .email("zerobase@naver.com")
                .name("zerobase")
                .password("1234")
                .verify(true)
                .build()));

        Customer customer = customerService.findValidCustomer( "zerobase@naver.com", "1234").get();

        assertEquals("zerobase@naver.com", customer.getEmail());
        assertEquals("zerobase", customer.getName());
        assertEquals("1234", customer.getPassword());
    }

    @Test
    void findValidCustomer_Not_Verify() {
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Customer.builder()
                .email("zerobase@naver.com")
                .name("zerobase")
                .password("1234")
                .verify(false)
                .build()));

        Optional<Customer> customer = customerService.findValidCustomer( "zerobase@naver.com", "1234");

        assertEquals(false, customer.isPresent());
    }

    @Test
    void findValidCustomer_No_Email() {
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Customer.builder()
                .email("zerobase@gmail.com")
                .name("zerobase")
                .password("1234")
                .verify(false)
                .build()));

        Optional<Customer> customer = customerService.findValidCustomer( "zerobase@naver.com", "1234");

        assertEquals(false, customer.isPresent());
    }
}