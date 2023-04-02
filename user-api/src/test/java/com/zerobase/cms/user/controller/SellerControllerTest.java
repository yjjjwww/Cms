package com.zerobase.cms.user.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SellerControllerTest {

    @MockBean
    private JwtAuthenticationProvider provider;

    @MockBean
    private SellerService sellerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getInfoSuccess() throws Exception {
        Seller seller = Seller.builder()
            .email("zerobase@naver.com")
            .id(1L)
            .build();

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(sellerService.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(seller));

        mockMvc.perform(get("/seller/getInfo")
                .header("X-AUTH-TOKEN", "token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("zerobase@naver.com"))
            .andExpect(jsonPath("$.id").value(1L))
            .andDo(print());
    }

    @Test
    void getInfoFail_NOT_FOUND_USER() throws Exception {

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(sellerService.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.empty());

        mockMvc.perform(get("/seller/getInfo")
                .header("X-AUTH-TOKEN", "token"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("NOT_FOUND_USER"))
            .andExpect(jsonPath("$.message").value("일치하는 회원이 없습니다."))
            .andDo(print());
    }
}