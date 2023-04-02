package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        AddProductForm form = AddProductForm.builder()
            .name("product")
            .description("description")
            .items(new ArrayList<>())
            .build();

        given(productRepository.save(any()))
            .willReturn(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(productItems)
                .build());

        Product result = productService.addProduct(anyLong(), form);

        assertEquals("product", result.getName());
        assertEquals(1L, result.getId());
        assertEquals("item", result.getProductItems().get(0).getName());
    }

    @Test
    void updateProductSuccess() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder()
            .id(1L)
            .name("item")
            .count(1)
            .price(50)
            .build());

        List<UpdateProductItemForm> itemForms = new ArrayList<>();
        itemForms.add(UpdateProductItemForm.builder()
            .id(1L)
            .productId(1L)
            .name("name")
            .price(1000)
            .count(3)
            .build());

        UpdateProductForm form = UpdateProductForm.builder()
            .id(1L)
            .name("zerobase")
            .description("new description")
            .items(itemForms)
            .build();

        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.ofNullable(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(productItems)
                .build()));

        Product result = productService.updateProduct(1L, form);

        assertEquals("zerobase", result.getName());
        assertEquals("name", result.getProductItems().get(0).getName());
        assertEquals(1000, result.getProductItems().get(0).getPrice());
    }

    @Test
    void updateProductFail_NOT_FOUND_PRODUCT() {
        UpdateProductForm form = UpdateProductForm.builder()
            .id(1L)
            .name("zerobase")
            .description("new description")
            .items(new ArrayList<>())
            .build();

        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> productService.updateProduct(1L, form));

        assertEquals(ErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode());
        assertEquals("상품을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void updateProductFail_NOT_FOUND_ITEM() {
        List<UpdateProductItemForm> itemForms = new ArrayList<>();
        itemForms.add(UpdateProductItemForm.builder()
            .id(1L)
            .productId(1L)
            .name("name")
            .price(1000)
            .count(3)
            .build());

        UpdateProductForm form = UpdateProductForm.builder()
            .id(1L)
            .name("zerobase")
            .description("new description")
            .items(itemForms)
            .build();

        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.ofNullable(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(new ArrayList<>())
                .build()));

        CustomException exception = assertThrows(CustomException.class,
            () -> productService.updateProduct(1L, form));

        assertEquals(ErrorCode.NOT_FOUND_ITEM, exception.getErrorCode());
        assertEquals("아이템을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void deleteProductSuccess() {
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.ofNullable(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("product")
                .description("description")
                .productItems(new ArrayList<>())
                .build()));

        Product result = productService.deleteProduct(1L, 1L);

        assertEquals(1L, result.getId());
        assertEquals("product", result.getName());
    }

    @Test
    void deleteProductFail_NOT_FOUND_PRODUCT() {
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> productService.deleteProduct(1L, 1L));

        assertEquals(ErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode());
        assertEquals("상품을 찾을 수 없습니다.", exception.getMessage());
    }
}