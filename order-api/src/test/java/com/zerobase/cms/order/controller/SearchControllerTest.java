package com.zerobase.cms.order.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.service.ProductSearchService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {

    @MockBean
    private ProductSearchService productSearchService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    void searchByName() throws Exception {
        List<Product> products = new ArrayList<>();
        List<ProductItem> productItems = new ArrayList<>();

        ProductItem productItem = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("name")
            .price(1000)
            .count(5)
            .build();
        productItems.add(productItem);

        Product product = Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("zerobase")
            .description("description")
            .productItems(productItems)
            .build();
        products.add(product);

        given(productSearchService.searchByName(anyString()))
            .willReturn(products);

        mockMvc.perform(get("/search/product?name=zerobase"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("zerobase"))
            .andDo(print());
    }

    @Test
    void getDetail() throws Exception {
        List<ProductItem> productItems = new ArrayList<>();

        ProductItem productItem = ProductItem.builder()
            .id(1L)
            .sellerId(1L)
            .name("name")
            .price(1000)
            .count(5)
            .build();
        productItems.add(productItem);

        Product product = Product.builder()
            .id(1L)
            .sellerId(1L)
            .name("zerobase")
            .description("description")
            .productItems(productItems)
            .build();

        given(productSearchService.getByProductId(anyLong()))
            .willReturn(product);

        mockMvc.perform(get("/search/product/detail?productId=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("zerobase"))
            .andExpect(jsonPath("$.items[0].name").value("name"))
            .andDo(print());
    }
}