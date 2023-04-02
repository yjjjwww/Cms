package com.zerobase.cms.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.redis.Cart.Product;
import com.zerobase.cms.order.domain.redis.Cart.ProductItem;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.ProductItemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderApplicationTest {

    @Mock
    private CartApplication cartApplication;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductItemService productItemService;

    @InjectMocks
    private OrderApplication orderApplication;

    @Test
    void orderSuccess() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("product")
            .description("description")
            .items(productItems)
            .build());

        given(cartApplication.refreshCart(any()))
            .willReturn(Cart.builder()
                .messages(new ArrayList<>())
                .products(products)
                .build());

        given(userClient.getCustomerInfo(anyString()))
            .willReturn(ResponseEntity.of(Optional.of(CustomerDto.builder()
                .balance(100)
                .build())));

        given(productItemService.getProductItem(anyLong()))
            .willReturn(com.zerobase.cms.order.domain.model.ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("item")
                .count(5)
                .price(50)
                .build());

        Cart result = orderApplication.order("token", any());

        assertEquals(1, result.getProducts().get(0).getItems().get(0).getCount());
    }

    @Test
    void orderFail_ORDER_FAIL_CHECK_CART() {
        List<String> messages = new ArrayList<>();
        messages.add("message");

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("product")
            .description("description")
            .items(productItems)
            .build());

        given(cartApplication.refreshCart(any()))
            .willReturn(Cart.builder()
                .messages(messages)
                .products(products)
                .build());

        CustomException exception = assertThrows(CustomException.class,
            () -> orderApplication.order("zerobase", any()));

        assertEquals(ErrorCode.ORDER_FAIL_CHECK_CART, exception.getErrorCode());
        assertEquals("주문 불가. 장바구니를 확인해 주세요.", exception.getMessage());
    }

    @Test
    void orderFail_ORDER_FAIL_NOT_ENOUGH_BALANCE() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("product")
            .description("description")
            .items(productItems)
            .build());

        given(userClient.getCustomerInfo(anyString()))
            .willReturn(ResponseEntity.of(Optional.of(CustomerDto.builder()
                .balance(10)
                .build())));

        given(cartApplication.refreshCart(any()))
            .willReturn(Cart.builder()
                .messages(new ArrayList<>())
                .products(products)
                .build());

        CustomException exception = assertThrows(CustomException.class,
            () -> orderApplication.order("zerobase", any()));

        assertEquals(ErrorCode.ORDER_FAIL_NOT_ENOUGH_BALANCE, exception.getErrorCode());
        assertEquals("주문 불가. 잔액 부족입니다.", exception.getMessage());
    }
}