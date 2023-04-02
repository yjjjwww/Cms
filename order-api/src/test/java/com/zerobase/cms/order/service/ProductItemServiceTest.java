package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductItemRepository;
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
class ProductItemServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @InjectMocks
    private ProductItemService productItemService;

    @Test
    void getProductItem() {
        given(productItemRepository.getById(anyLong()))
            .willReturn(ProductItem.builder()
                .id(1L)
                .name("item")
                .count(1)
                .price(50)
                .build());

        ProductItem result = productItemService.getProductItem(1L);

        assertEquals(1L, result.getId());
        assertEquals("item", result.getName());
    }

    @Test
    void saveProductItem() {
        given(productItemRepository.save(any()))
            .willReturn(ProductItem.builder()
                .id(1L)
                .name("item")
                .count(1)
                .price(50)
                .build());

        ProductItem result = productItemService.saveProductItem(ProductItem.builder().build());

        assertEquals(1L, result.getId());
        assertEquals("item", result.getName());
    }

    @Test
    void addProductItemSuccess() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.ofNullable(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(productItems)
                .build()));

        AddProductItemForm form = AddProductItemForm.builder()
            .productId(1L)
            .name("zero")
            .price(1000)
            .count(10)
            .build();

        Product result = productItemService.addProductItem(1L, form);

        assertEquals(2, result.getProductItems().size());
        assertEquals("zero", result.getProductItems().get(1).getName());
    }

    @Test
    void addProductItemFail_NOT_FOUND_PRODUCT() {
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        AddProductItemForm form = AddProductItemForm.builder()
            .productId(1L)
            .name("zero")
            .price(1000)
            .count(10)
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> productItemService.addProductItem(1L, form));

        assertEquals(ErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode());
        assertEquals("상품을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void addProductItemFail_SAME_ITEM_NAME() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.ofNullable(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(productItems)
                .build()));

        AddProductItemForm form = AddProductItemForm.builder()
            .productId(1L)
            .name("item")
            .price(1000)
            .count(10)
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> productItemService.addProductItem(1L, form));

        assertEquals(ErrorCode.SAME_ITEM_NAME, exception.getErrorCode());
        assertEquals("아이템 명 중복입니다.", exception.getMessage());
    }

    @Test
    void updateProductItemSuccess() {
        given(productItemRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("item")
                .count(1)
                .price(50)
                .build()));

        UpdateProductItemForm form = UpdateProductItemForm.builder()
            .id(1L)
            .productId(1L)
            .name("zero")
            .price(1000)
            .count(3)
            .build();

        ProductItem result = productItemService.updateProductItem(1L, form);

        assertEquals("zero", result.getName());
        assertEquals(1000, result.getPrice());
    }

    @Test
    void updateProductItemFail_NOT_FOUND_ITEM() {
        given(productItemRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("item")
                .count(1)
                .price(50)
                .build()));

        UpdateProductItemForm form = UpdateProductItemForm.builder()
            .id(1L)
            .productId(1L)
            .name("zero")
            .price(1000)
            .count(3)
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> productItemService.updateProductItem(10L, form));

        assertEquals(ErrorCode.NOT_FOUND_ITEM, exception.getErrorCode());
        assertEquals("아이템을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void deleteProductItemSuccess() {
        given(productItemRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("item")
                .count(1)
                .price(50)
                .build()));

        ProductItem result = productItemService.deleteProductItem(1L, 1L);

        assertEquals("item", result.getName());
        assertEquals(50, result.getPrice());
    }

    @Test
    void deleteProductItemFail_NOT_FOUND_ITEM() {
        given(productItemRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("item")
                .count(1)
                .price(50)
                .build()));

        CustomException exception = assertThrows(CustomException.class,
            () -> productItemService.deleteProductItem(10L, 1L));

        assertEquals(ErrorCode.NOT_FOUND_ITEM, exception.getErrorCode());
        assertEquals("아이템을 찾을 수 없습니다.", exception.getMessage());
    }
}