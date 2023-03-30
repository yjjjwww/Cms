package com.zerobase.cms.order.application;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartApplication {
    public final CartService cartService;
    private final ProductSearchService productSearchService;;

    public Cart addCart(Long customerId, AddProductCartForm form){
        Product product = productSearchService.getByProductId(form.getId());
        if(product == null){
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }

        Map<Long, Object> itemCountMap = product.getProductItems().stream()
            .collect(Collectors.toMap(ProductItem::getId, it->it));

        if(form.getItems().stream().anyMatch(formItem -> !itemCountMap.containsKey(formItem.getId()))){
            throw new CustomException(ErrorCode.NOT_FOUND_ITEM);
        }

        Cart cart = cartService.getCart(customerId);

        if(!addable(cart, product, form)){
            throw new CustomException(ErrorCode.NOT_ENOUGH_ITEM_COUNT);
        }

        return cartService.addCart(customerId, form);
    }

    private boolean addable(Cart cart, Product product, AddProductCartForm form){
        Map<Long, Integer> dbItemCountMap = product.getProductItems().stream()
            .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        if(cart == null){
            return form.getItems().stream().noneMatch(
                formItem -> formItem.getCount() > dbItemCountMap.get(formItem.getId())
            );
        }

        Cart.Product cartProduct = cart.getProducts().stream()
            .filter(p -> p.getId().equals(form.getId()))
            .findFirst()
            .orElse(null);

        if(cartProduct == null){
            return form.getItems().stream().noneMatch(
                formItem -> formItem.getCount() > dbItemCountMap.get(formItem.getId())
            );
        }

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
            .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        return form.getItems().stream().noneMatch(
            formItem -> {
                Integer cartCount = cartItemCountMap.get(formItem.getId());
                Integer dbCount = dbItemCountMap.get(formItem.getId());
                return formItem.getCount() + cartCount > dbCount;
            }
        );
    }
}
