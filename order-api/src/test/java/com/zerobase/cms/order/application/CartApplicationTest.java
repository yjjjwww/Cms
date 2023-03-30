package com.zerobase.cms.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CartApplicationTest {
    @Autowired
    CartApplication cartApplication;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Test
    void addCart(){
        Long sellerId = 1L;
        Long customerId = 100L;
        Product product = add_product();
        clearCart(customerId);

        AddProductCartForm form = makeAddForm(product, 20000, 2);
        Cart cart = cartApplication.addCart(customerId, form);

        assertEquals(customerId, cart.getCustomerId());
        assertEquals(sellerId, cart.getProducts().get(0).getSellerId());
        assertEquals("나이키", cart.getProducts().get(0).getName());
        assertEquals("나이키0", cart.getProducts().get(0).getItems().get(0).getName());
        assertEquals(20000, cart.getProducts().get(0).getItems().get(0).getPrice());
        assertEquals(2, cart.getProducts().get(0).getItems().get(0).getCount());
    }

    @Transactional
    @Test
    void addAndRefreshCart(){
        Long sellerId = 1L;
        Long customerId = 100L;
        Product product = add_product();
        clearCart(customerId);

        AddProductCartForm form = makeAddForm(product, 20000, 9);
        Cart cart = cartApplication.addCart(customerId, form);

        assertEquals(customerId, cart.getCustomerId());
        assertEquals(sellerId, cart.getProducts().get(0).getSellerId());
        assertEquals("나이키", cart.getProducts().get(0).getName());
        assertEquals("나이키0", cart.getProducts().get(0).getItems().get(0).getName());
        assertEquals(20000, cart.getProducts().get(0).getItems().get(0).getPrice());
        assertEquals(9, cart.getProducts().get(0).getItems().get(0).getCount());

        product.getProductItems().get(0).setCount(1);
        productRepository.save(product);

        cart = cartApplication.getCart(customerId);

        assertEquals(customerId, cart.getCustomerId());
        assertEquals(sellerId, cart.getProducts().get(0).getSellerId());
        assertEquals("나이키", cart.getProducts().get(0).getName());
        assertEquals("나이키0", cart.getProducts().get(0).getItems().get(0).getName());
        assertEquals(10000, cart.getProducts().get(0).getItems().get(0).getPrice());
        assertEquals(1, cart.getProducts().get(0).getItems().get(0).getCount());
    }

    void clearCart(Long customerId){
        cartApplication.clearCart(customerId);
    }

    AddProductCartForm makeAddForm(Product product, int price, int count){
        AddProductCartForm.ProductItem productItem =
            AddProductCartForm.ProductItem.builder()
                .id(product.getProductItems().get(0).getId())
                .name(product.getProductItems().get(0).getName())
                .price(price)
                .count(count)
                .build();

        return AddProductCartForm.builder()
            .id(product.getId())
            .sellerId(product.getSellerId())
            .name(product.getName())
            .description(product.getDescription())
            .items(List.of(productItem))
            .build();

    }

    Product add_product(){
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키", "신발", 3);

        Product product = productService.addProduct(sellerId, form);

        return productRepository.findWithProductItemsById(product.getId()).get();
    }

    private static AddProductForm makeProductForm(String name, String description, int itemCount){
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for(int i = 0; i < itemCount; i++){
            itemForms.add(makeProductItemForm((long) (i + 1), name+i));
        }
        return AddProductForm.builder()
            .name(name)
            .description(description)
            .items(itemForms)
            .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
            .productId(productId)
            .name(name)
            .price(10000)
            .count(10)
            .build();
    }


}