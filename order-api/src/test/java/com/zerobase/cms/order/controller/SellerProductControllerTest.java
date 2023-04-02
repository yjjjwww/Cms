package com.zerobase.cms.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.service.ProductService;
import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SellerProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductItemService productItemService;

    @MockBean
    private JwtAuthenticationProvider provider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addProduct() throws Exception {
        AddProductItemForm itemForm = AddProductItemForm.builder()
            .productId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        ProductItem item = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(item);

        List<AddProductItemForm> itemForms = new ArrayList<>();
        itemForms.add(itemForm);

        AddProductForm form = AddProductForm.builder()
            .name("zerobase")
            .description("description")
            .items(itemForms)
            .build();

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(productService.addProduct(anyLong(), any()))
            .willReturn(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("zerobase")
                .description("description")
                .productItems(productItems)
                .build());

        mockMvc.perform(post("/seller/product")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andDo(print());
    }

    @Test
    void addProductItem() throws Exception {
        AddProductItemForm itemForm = AddProductItemForm.builder()
            .productId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        ProductItem item = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(item);

        List<AddProductItemForm> itemForms = new ArrayList<>();
        itemForms.add(itemForm);

        AddProductForm form = AddProductForm.builder()
            .name("zerobase")
            .description("description")
            .items(itemForms)
            .build();

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(productItemService.addProductItem(anyLong(), any()))
            .willReturn(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("zerobase")
                .description("description")
                .productItems(productItems)
                .build());

        mockMvc.perform(post("/seller/product/item")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andDo(print());
    }

    @Test
    void updateProduct() throws Exception {
        UpdateProductForm form = UpdateProductForm.builder()
            .id(1L)
            .name("name")
            .description("description")
            .build();

        ProductItem item = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(item);

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(productService.updateProduct(anyLong(), any()))
            .willReturn(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("zerobase")
                .description("description")
                .productItems(productItems)
                .build());

        mockMvc.perform(put("/seller/product")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andDo(print());
    }

    @Test
    void updateProductItem() throws Exception {
        UpdateProductItemForm form = UpdateProductItemForm.builder()
            .id(1L)
            .productId(1L)
            .name("name")
            .price(100)
            .count(10)
            .build();

        ProductItem item = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(item);

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(productItemService.updateProductItem(anyLong(), any()))
            .willReturn(ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("zerobase")
                .price(1000)
                .count(10)
                .build());

        mockMvc.perform(put("/seller/product/item")
                .header("X-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andDo(print());
    }

    @Test
    void deleteProduct() throws Exception {

        ProductItem item = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(item);

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(productService.deleteProduct(anyLong(), any()))
            .willReturn(Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("zerobase")
                .description("description")
                .productItems(productItems)
                .build());

        mockMvc.perform(delete("/seller/product?id=1")
                .header("X-AUTH-TOKEN", "token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andDo(print());
    }

    @Test
    void deleteProductItem() throws Exception {

        ProductItem item = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("item1")
            .price(1000)
            .count(1)
            .build();

        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(item);

        given(provider.getUserVo(anyString()))
            .willReturn(new UserVo(1L, "zerobase@naver.com"));

        given(productItemService.deleteProductItem(anyLong(), any()))
            .willReturn(ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("zerobase")
                .price(1000)
                .count(10)
                .build());

        mockMvc.perform(delete("/seller/product/item?id=1")
                .header("X-AUTH-TOKEN", "token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andDo(print());
    }
}