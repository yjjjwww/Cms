package com.zerobase.cms.order.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.order.application.CartApplication;
import com.zerobase.cms.order.application.OrderApplication;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerCartControllerTest {

    @MockBean
    private CartApplication cartApplication;

    @MockBean
    private JwtAuthenticationProvider provider;

    @MockBean
    private OrderApplication orderApplication;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCart() throws Exception {
        AddProductCartForm form = AddProductCartForm.builder()
            .id(1L)
            .sellerId(1L)
            .name("name")
            .description("description")
            .items(new ArrayList<>())
            .build();

        given(cartApplication.addCart(anyLong(), any()))
            .willReturn(Cart.builder()
                .customerId(1L)
                .products(new ArrayList<>())
                .messages(new ArrayList<>())
                .build());

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        mockMvc.perform(post("/customer/cart")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(1L))
            .andDo(print());
    }

    @Test
    void showCart() throws Exception {
        given(cartApplication.getCart(anyLong()))
            .willReturn(Cart.builder()
                .customerId(1L)
                .products(new ArrayList<>())
                .messages(new ArrayList<>())
                .build());

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        mockMvc.perform(get("/customer/cart")
                .header("X-AUTH-TOKEN", "token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(1L))
            .andDo(print());
    }

    @Test
    void updateCart() throws Exception {
        Cart cart = Cart.builder()
            .customerId(1L)
            .products(new ArrayList<>())
            .messages(new ArrayList<>())
            .build();

        given(cartApplication.updateCart(anyLong(), any()))
            .willReturn(cart);

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        mockMvc.perform(put("/customer/cart")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(1L))
            .andDo(print());
    }

    @Test
    void order() throws Exception {
        Cart cart = Cart.builder()
            .customerId(1L)
            .products(new ArrayList<>())
            .messages(new ArrayList<>())
            .build();

        given(orderApplication.order(anyString(), any()))
            .willReturn(cart);

        given(orderApplication.sendOrderMail(anyString(), any()))
            .willReturn("result");

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        MvcResult result = mockMvc.perform(post("/customer/cart/order")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "result");
    }
}