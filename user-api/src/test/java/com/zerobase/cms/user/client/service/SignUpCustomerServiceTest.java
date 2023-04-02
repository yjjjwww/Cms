package com.zerobase.cms.user.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignUpCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SignUpCustomerService signUpCustomerService;

    @Test
    void signUp() {
        SignUpForm form = SignUpForm.builder()
            .name("name")
            .birth(LocalDate.now())
            .email("abc@gmail.com")
            .password("1")
            .phone("01000000000")
            .build();

        given(customerRepository.save(any()))
            .willReturn(Customer.from(form));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        Customer customer = signUpCustomerService.signUp(form);

        verify(customerRepository, times(1)).save(captor.capture());
        assertEquals("name", customer.getName());
    }

    @Test
    void isEmailExist() {
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(Customer.builder().build()));

        boolean result = signUpCustomerService.isEmailExist("email");

        assertEquals(true, result);
    }

    @Test
    void isEmailNotExist() {
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        boolean result = signUpCustomerService.isEmailExist("email");

        assertEquals(false, result);
    }

    @Test
    void verifyEmailSuccess() {
        Customer customerBeforeVerify = Customer.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(customerBeforeVerify));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        signUpCustomerService.verifyEmail("zerobase@naver.com", "0000");

        verify(customerRepository, times(1)).save(captor.capture());
    }

    @Test
    void verifyEmailFail_NOT_FOUND_USER() {
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpCustomerService.verifyEmail("zerobase", "1111"));

        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
        assertEquals("일치하는 회원이 없습니다.", exception.getMessage());
    }

    @Test
    void verifyEmailFail_ALREADY_VERIFY() {
        Customer customerBeforeVerify = Customer.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode("0000")
            .verify(true)
            .build();

        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(customerBeforeVerify));

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpCustomerService.verifyEmail("zerobase", "1111"));

        assertEquals(ErrorCode.ALREADY_VERIFY, exception.getErrorCode());
        assertEquals("이미 인증이 완료되었습니다.", exception.getMessage());
    }

    @Test
    void verifyEmailFail_WRONG_VERIFICATION() {
        Customer customerBeforeVerify = Customer.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(customerBeforeVerify));

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpCustomerService.verifyEmail("zerobase", "1111"));

        assertEquals(ErrorCode.WRONG_VERIFICATION, exception.getErrorCode());
        assertEquals("잘못된 인증 시도입니다.", exception.getMessage());
    }

    @Test
    void verifyEmailFail_EXPIRE_CODE() {
        Customer customerBeforeVerify = Customer.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().minusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(customerBeforeVerify));

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpCustomerService.verifyEmail("zerobase", "0000"));

        assertEquals(ErrorCode.EXPIRE_CODE, exception.getErrorCode());
        assertEquals("인증 시간이 만료되었습니다.", exception.getMessage());
    }

    @Test
    void changeCustomerValidateEmail() {
        Customer customer = Customer.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().minusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(customerRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(customer));

        LocalDateTime result = signUpCustomerService.changeCustomerValidateEmail(1L, "0000");

        assertEquals(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), result.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}