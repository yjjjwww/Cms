package com.zerobase.cms.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.customer.CustomerService;
import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.common.UserType;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignInApplicationTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private SellerService sellerService;

    @Mock
    private JwtAuthenticationProvider provider;

    @InjectMocks
    private SignInApplication signInApplication;

    @Test
    void customerLoginTokenSuccess() {
        SignInForm form = new SignInForm("zerobase@naver.com", "1234");

        given(customerService.findValidCustomer(anyString(), anyString()))
            .willReturn(Optional.ofNullable(Customer.builder()
                    .email("zerobase@naver.com")
                    .id(1L)
                .build()));

        String token = provider.createToken("zerobase@naver.com", 1L, UserType.CUSTOMER);

        given(provider.createToken(anyString(), anyLong(), any()))
            .willReturn(token);

        String result = signInApplication.customerLoginToken(form);

        assertEquals(token, result);
    }

    @Test
    void customerLoginTokenFail_LOGIN_CHECK_FAIL() {
        SignInForm form = new SignInForm("zerobase@naver.com", "1234");

        given(customerService.findValidCustomer(anyString(), anyString()))
            .willThrow(new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        CustomException exception = assertThrows(CustomException.class,
            () -> signInApplication.customerLoginToken(form));

        assertEquals(ErrorCode.LOGIN_CHECK_FAIL, exception.getErrorCode());
        assertEquals("아이디나 패스워드를 확인해 주세요.", exception.getMessage());
    }

    @Test
    void sellerLoginTokenSuccess() {
        SignInForm form = new SignInForm("zerobase@naver.com", "1234");

        given(sellerService.findValidSeller(anyString(), anyString()))
            .willReturn(Optional.ofNullable(Seller.builder()
                .email("zerobase@naver.com")
                .id(1L)
                .build()));

        String token = provider.createToken("zerobase@naver.com", 1L, UserType.CUSTOMER);

        given(provider.createToken(anyString(), anyLong(), any()))
            .willReturn(token);

        String result = signInApplication.sellerLoginToken(form);

        assertEquals(token, result);
    }

    @Test
    void sellerLoginTokenFail_LOGIN_CHECK_FAIL() {
        SignInForm form = new SignInForm("zerobase@naver.com", "1234");

        given(sellerService.findValidSeller(anyString(), anyString()))
            .willThrow(new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        CustomException exception = assertThrows(CustomException.class,
            () -> signInApplication.sellerLoginToken(form));

        assertEquals(ErrorCode.LOGIN_CHECK_FAIL, exception.getErrorCode());
        assertEquals("아이디나 패스워드를 확인해 주세요.", exception.getMessage());
    }
}