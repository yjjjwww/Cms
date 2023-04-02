package com.zerobase.cms.user.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import com.zerobase.cms.user.service.seller.SellerService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignUpApplicationTest {

    @Mock
    private MailgunClient mailgunClient;

    @Mock
    private SignUpCustomerService signUpCustomerService;

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SignUpApplication signUpApplication;

    @Test
    void customerSignUpSuccess() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("a123456@#")
            .phone("01011112222")
            .build();

        given(signUpCustomerService.signUp(form))
            .willReturn(Customer.from(form));

        assertEquals(signUpApplication.customerSignUp(form), "회원 가입에 성공하였습니다.");
    }

    @Test
    void customerSignUpFail_ALREADY_REGISTER_USER() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("a123456@#")
            .phone("01011112222")
            .build();

        given(signUpCustomerService.isEmailExist(form.getEmail()))
            .willReturn(true);

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.customerSignUp(form));

        assertEquals(ErrorCode.ALREADY_REGISTER_USER, exception.getErrorCode());
        assertEquals("이미 가입한 회원입니다.", exception.getMessage());
    }

    @Test
    void customerSignUpFail_NOT_VALID_EMAIL() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase")
            .name("name")
            .password("a123456@#")
            .phone("01011112222")
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.customerSignUp(form));

        assertEquals(ErrorCode.NOT_VALID_EMAIL, exception.getErrorCode());
        assertEquals("이메일 형식을 확인해주세요.", exception.getMessage());
    }

    @Test
    void customerSignUpFail_NOT_VALID_PHONE() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("a123456@#")
            .phone("2345")
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.customerSignUp(form));

        assertEquals(ErrorCode.NOT_VALID_PHONE, exception.getErrorCode());
        assertEquals("핸드폰 번호 형식을 확인해주세요.", exception.getMessage());
    }

    @Test
    void customerSignUpFail_NOT_VALID_PASSWORD() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("1111")
            .phone("01011112222")
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.customerSignUp(form));

        assertEquals(ErrorCode.NOT_VALID_PASSWORD, exception.getErrorCode());
        assertEquals("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상 포함해야 합니다.", exception.getMessage());
    }

    @Test
    void sellerSignUpSuccess() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("a123456@#")
            .phone("01011112222")
            .build();

        given(sellerService.signUp(form))
            .willReturn(Seller.from(form));

        assertEquals(signUpApplication.sellerSignUp(form), "회원 가입에 성공하였습니다.");
    }

    @Test
    void sellerSignUpFail_ALREADY_REGISTER_USER() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("a123456@#")
            .phone("01011112222")
            .build();

        given(sellerService.isEmailExist(form.getEmail()))
            .willReturn(true);

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.sellerSignUp(form));

        assertEquals(ErrorCode.ALREADY_REGISTER_USER, exception.getErrorCode());
        assertEquals("이미 가입한 회원입니다.", exception.getMessage());
    }

    @Test
    void sellerSignUpFail_NOT_VALID_EMAIL() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase")
            .name("name")
            .password("a123456@#")
            .phone("01011112222")
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.sellerSignUp(form));

        assertEquals(ErrorCode.NOT_VALID_EMAIL, exception.getErrorCode());
        assertEquals("이메일 형식을 확인해주세요.", exception.getMessage());
    }

    @Test
    void sellerSignUpFail_NOT_VALID_PHONE() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("a123456@#")
            .phone("2345")
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.sellerSignUp(form));

        assertEquals(ErrorCode.NOT_VALID_PHONE, exception.getErrorCode());
        assertEquals("핸드폰 번호 형식을 확인해주세요.", exception.getMessage());
    }

    @Test
    void sellerSignUpFail_NOT_VALID_PASSWORD() {
        SignUpForm form = SignUpForm.builder()
            .birth(LocalDate.now())
            .email("zerobase@naver.com")
            .name("name")
            .password("1111")
            .phone("01011112222")
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> signUpApplication.sellerSignUp(form));

        assertEquals(ErrorCode.NOT_VALID_PASSWORD, exception.getErrorCode());
        assertEquals("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상 포함해야 합니다.", exception.getMessage());
    }
}