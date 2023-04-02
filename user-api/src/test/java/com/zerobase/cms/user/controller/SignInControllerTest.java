package com.zerobase.cms.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.user.application.SignInApplication;
import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SignInControllerTest {

    @MockBean
    private SignInApplication signInApplication;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signInCustomerSuccess() throws Exception {
        given(signInApplication.customerLoginToken(any()))
            .willReturn("code");

        mockMvc.perform(post("/signIn/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignInForm("yjjjjwww@naver.com", "name")
                )))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void signInCustomerFail_LOGIN_CHECK_FAIL() throws Exception {
        given(signInApplication.customerLoginToken(any()))
            .willThrow(new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        mockMvc.perform(post("/signIn/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignInForm("yjjjjwww@naver.com", "name")
                )))
            .andExpect(jsonPath("$.errorCode").value("LOGIN_CHECK_FAIL"))
            .andExpect(jsonPath("$.message").value("아이디나 패스워드를 확인해 주세요."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    void signInSellerSuccess() throws Exception {
        given(signInApplication.sellerLoginToken(any()))
            .willReturn("code");

        mockMvc.perform(post("/signIn/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignInForm("yjjjjwww@naver.com", "name")
                )))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void signInSellerFail_LOGIN_CHECK_FAIL() throws Exception {
        given(signInApplication.sellerLoginToken(any()))
            .willThrow(new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        mockMvc.perform(post("/signIn/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new SignInForm("yjjjjwww@naver.com", "name")
                )))
            .andExpect(jsonPath("$.errorCode").value("LOGIN_CHECK_FAIL"))
            .andExpect(jsonPath("$.message").value("아이디나 패스워드를 확인해 주세요."))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }
}