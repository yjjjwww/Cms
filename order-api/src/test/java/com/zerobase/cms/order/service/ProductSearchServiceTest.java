package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    @Test
    void searchByName() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("product")
            .description("description")
            .productItems(new ArrayList<>())
            .build());

        given(productRepository.searchByName(anyString()))
            .willReturn(products);

        List<Product> result = productSearchService.searchByName("zerobase");

        assertEquals("product", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getByProductIdSuccess() {
        given(productRepository.findWithProductItemsById(anyLong()))
            .willReturn(Optional.ofNullable(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(new ArrayList<>())
                .build()));

        Product result = productSearchService.getByProductId(1L);

        assertEquals("product", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void getByProductIdFail() {
        given(productRepository.findWithProductItemsById(anyLong()))
            .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> productSearchService.getByProductId(1L));

        assertEquals(ErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode());
        assertEquals("상품을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void getListByProductIds() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("product")
            .description("description")
            .productItems(new ArrayList<>())
            .build());

        given(productRepository.findAllByIdIn(any()))
            .willReturn(products);

        List<Long> productIds = new ArrayList<>();
        productIds.add(1L);

        List<Product> result = productSearchService.getListByProductIds(productIds);

        assertEquals("product", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
    }
}