package com.zerobase.cms.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.user.application.SignUpApplication;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTest {

    @MockBean
    private SignUpApplication signUpApplication;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void customerSignUpSuccess() throws Exception {
        given(signUpApplication.customerSignUp(any()))
            .willReturn("회원 가입에 성공하였습니다.");

        mockMvc.perform(post("/signup/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void customerSignUpFail_ALREADY_REGISTER_USER() throws Exception {
        given(signUpApplication.customerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.ALREADY_REGISTER_USER));

        mockMvc.perform(post("/signup/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("ALREADY_REGISTER_USER"))
            .andExpect(jsonPath("$.message").value("이미 가입한 회원입니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void customerSignUpFail_NOT_VALID_EMAIL() throws Exception {
        given(signUpApplication.customerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.NOT_VALID_EMAIL));

        mockMvc.perform(post("/signup/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("NOT_VALID_EMAIL"))
            .andExpect(jsonPath("$.message").value("이메일 형식을 확인해주세요."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void customerSignUpFail_NOT_VALID_PHONE() throws Exception {
        given(signUpApplication.customerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.NOT_VALID_PHONE));

        mockMvc.perform(post("/signup/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("NOT_VALID_PHONE"))
            .andExpect(jsonPath("$.message").value("핸드폰 번호 형식을 확인해주세요."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void customerSignUpFail_NOT_VALID_PASSWORD() throws Exception {
        given(signUpApplication.customerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.NOT_VALID_PASSWORD));

        mockMvc.perform(post("/signup/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("NOT_VALID_PASSWORD"))
            .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상 포함해야 합니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifyCustomerSuccess() throws Exception {
        mockMvc.perform(get("/signup/customer/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void verifyCustomerFail_NOT_FOUND_USER() throws Exception {
        doThrow(new CustomException(ErrorCode.NOT_FOUND_USER)).when(signUpApplication)
            .customerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/customer/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("NOT_FOUND_USER"))
            .andExpect(jsonPath("$.message").value("일치하는 회원이 없습니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifyCustomerFail_ALREADY_VERIFY() throws Exception {
        doThrow(new CustomException(ErrorCode.ALREADY_VERIFY)).when(signUpApplication)
            .customerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/customer/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("ALREADY_VERIFY"))
            .andExpect(jsonPath("$.message").value("이미 인증이 완료되었습니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifyCustomerFail_WRONG_VERIFICATION() throws Exception {
        doThrow(new CustomException(ErrorCode.WRONG_VERIFICATION)).when(signUpApplication)
            .customerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/customer/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("WRONG_VERIFICATION"))
            .andExpect(jsonPath("$.message").value("잘못된 인증 시도입니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifyCustomerFail_EXPIRE_CODE() throws Exception {
        doThrow(new CustomException(ErrorCode.EXPIRE_CODE)).when(signUpApplication)
            .customerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/customer/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("EXPIRE_CODE"))
            .andExpect(jsonPath("$.message").value("인증 시간이 만료되었습니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void sellerSignUpSuccess() throws Exception {
        given(signUpApplication.sellerSignUp(any()))
            .willReturn("회원 가입에 성공하였습니다.");

        mockMvc.perform(post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void sellerSignUpFail_ALREADY_REGISTER_USER() throws Exception {
        given(signUpApplication.sellerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.ALREADY_REGISTER_USER));

        mockMvc.perform(post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("ALREADY_REGISTER_USER"))
            .andExpect(jsonPath("$.message").value("이미 가입한 회원입니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void sellerSignUpFail_NOT_VALID_EMAIL() throws Exception {
        given(signUpApplication.sellerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.NOT_VALID_EMAIL));

        mockMvc.perform(post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("NOT_VALID_EMAIL"))
            .andExpect(jsonPath("$.message").value("이메일 형식을 확인해주세요."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void sellerSignUpFail_NOT_VALID_PHONE() throws Exception {
        given(signUpApplication.sellerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.NOT_VALID_PHONE));

        mockMvc.perform(post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("NOT_VALID_PHONE"))
            .andExpect(jsonPath("$.message").value("핸드폰 번호 형식을 확인해주세요."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void sellerSignUpFail_NOT_VALID_PASSWORD() throws Exception {
        given(signUpApplication.sellerSignUp(any()))
            .willThrow(new CustomException(ErrorCode.NOT_VALID_PASSWORD));

        mockMvc.perform(post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignUpForm("yjjjjwww@naver.com", "name", "123456!@", LocalDate.now(),
                        "01011112222")
                )))
            .andExpect(jsonPath("$.errorCode").value("NOT_VALID_PASSWORD"))
            .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상 포함해야 합니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifySellerSuccess() throws Exception {
        mockMvc.perform(get("/signup/seller/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void verifySellerFail_NOT_FOUND_USER() throws Exception {
        doThrow(new CustomException(ErrorCode.NOT_FOUND_USER)).when(signUpApplication)
            .sellerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/seller/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("NOT_FOUND_USER"))
            .andExpect(jsonPath("$.message").value("일치하는 회원이 없습니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifySellerFail_ALREADY_VERIFY() throws Exception {
        doThrow(new CustomException(ErrorCode.ALREADY_VERIFY)).when(signUpApplication)
            .sellerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/seller/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("ALREADY_VERIFY"))
            .andExpect(jsonPath("$.message").value("이미 인증이 완료되었습니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifySellerFail_WRONG_VERIFICATION() throws Exception {
        doThrow(new CustomException(ErrorCode.WRONG_VERIFICATION)).when(signUpApplication)
            .sellerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/seller/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("WRONG_VERIFICATION"))
            .andExpect(jsonPath("$.message").value("잘못된 인증 시도입니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void verifySellerFail_EXPIRE_CODE() throws Exception {
        doThrow(new CustomException(ErrorCode.EXPIRE_CODE)).when(signUpApplication)
            .sellerVerify(anyString(), anyString());

        mockMvc.perform(get("/signup/seller/verify?email=yjjjwww@naver.com&code=1111"))
            .andExpect(jsonPath("$.errorCode").value("EXPIRE_CODE"))
            .andExpect(jsonPath("$.message").value("인증 시간이 만료되었습니다."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }
}