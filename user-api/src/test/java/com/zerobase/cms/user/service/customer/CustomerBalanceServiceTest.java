package com.zerobase.cms.user.service.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.user.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.CustomerBalanceHistory;
import com.zerobase.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerBalanceServiceTest {

    @Mock
    private CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerBalanceService customerBalanceService;

    @Test
    void changeBalanceSuccess() {
        ChangeBalanceForm form = new ChangeBalanceForm("zerobase", "zerobase backend", -3000);

        Customer customer = Customer.builder()
            .balance(10000)
            .build();

        CustomerBalanceHistory history = CustomerBalanceHistory.builder()
            .customer(customer)
            .changeMoney(-3000)
            .currentMoney(7000)
            .build();

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(anyLong()))
            .willReturn(Optional.ofNullable(history));

        given(customerRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(customer));

        given(customerBalanceHistoryRepository.save(any()))
            .willReturn(history);

        customerBalanceService.changeBalance(1L, form);

        ArgumentCaptor<CustomerBalanceHistory> captor = ArgumentCaptor.forClass(
            CustomerBalanceHistory.class);

        verify(customerBalanceHistoryRepository, times(1)).save(captor.capture());

        assertEquals(history.getCurrentMoney(), 7000);
        assertEquals(history.getChangeMoney(), -3000);
    }

    @Test
    void changeBalanceFail_NOT_ENOUGH_BALANCE() {
        ChangeBalanceForm form = new ChangeBalanceForm("zerobase", "zerobase backend", -3000);

        Customer customer = Customer.builder()
            .balance(1000)
            .build();

        CustomerBalanceHistory history = CustomerBalanceHistory.builder()
            .customer(customer)
            .changeMoney(-3000)
            .currentMoney(1000)
            .build();

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(anyLong()))
            .willReturn(Optional.ofNullable(history));

        given(customerRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(customer));

        CustomException exception = assertThrows(CustomException.class,
            () -> customerBalanceService.changeBalance(1L, form));

        assertEquals(ErrorCode.NOT_ENOUGH_BALANCE, exception.getErrorCode());
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    void changeBalanceFail_NOT_FOUND_USER() {
        ChangeBalanceForm form = new ChangeBalanceForm("zerobase", "zerobase backend", -3000);

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(anyLong()))
            .willReturn(Optional.empty());

        given(customerRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> customerBalanceService.changeBalance(1L, form));

        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
        assertEquals("일치하는 회원이 없습니다.", exception.getMessage());
    }
}