package com.zerobase.cms.order.application;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import java.util.ArrayList;
import java.util.List;
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

        if(cart != null && !addable(cart, product, form)){
            throw new CustomException(ErrorCode.NOT_ENOUGH_ITEM_COUNT);
        }

        return cartService.addCart(customerId, form);
    }

    public void clearCart(Long customerId){
        cartService.putCart(customerId, null);
    }

    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));
        Cart returnCart = new Cart(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());
        cartService.putCart(customerId, cart);
        return returnCart;
    }

    public Cart updateCart(Long customerId, Cart cart){
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }

    protected Cart refreshCart(Cart cart){
        Map<Long, Product> productMap = productSearchService.getListByProductIds(cart.getProducts()
                .stream().map(Cart.Product::getId).collect(Collectors.toList()))
            .stream().collect(Collectors.toMap(Product::getId, product -> product));

        for(int i = 0; i < cart.getProducts().size(); i++){
            Cart.Product cartProduct = cart.getProducts().get(i);
            Product product = productMap.get(cartProduct.getId());
            if(product == null){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> itemMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, item -> item));

            List<String> tmpMessages = new ArrayList<>();
            for(int j = 0; j < cartProduct.getItems().size(); j++){
                Cart.ProductItem cartItem = cartProduct.getItems().get(j);
                ProductItem productItem = itemMap.get(cartItem.getId());
                if(productItem == null){
                    cartProduct.getItems().remove(cartItem);
                    j--;
                    tmpMessages.add(cartItem.getName() + "옵션이 삭제되었습니다.");
                    continue;
                }
                boolean isPriceChanged = false, isCountNotEnough = false;
                if(!cartItem.getPrice().equals(productItem.getPrice())){
                    isPriceChanged = true;
                    cartItem.setPrice(productItem.getPrice());
                }

                if(cartItem.getCount() > productItem.getCount()){
                    isCountNotEnough = true;
                    cartItem.setCount(productItem.getCount());
                }

                if(isPriceChanged && isCountNotEnough){
                    tmpMessages.add(cartItem.getName() + "의 가격과 수량이 변동되었습니다.");
                } else if (isPriceChanged){
                    tmpMessages.add(cartItem.getName() + "의 가격이 변동되었습니다.");
                } else if (isCountNotEnough){
                    tmpMessages.add(cartItem.getName() + "의 수량이 변동되었습니다.");
                }
            }
            if(cartProduct.getItems().isEmpty()){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품의 옵션이 모두 없어져 구매가 불가능합니다.");
            } else if(tmpMessages.size() > 0){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(cartProduct.getName()).append(" 상품의 변동 사항: ");
                for(String msg : tmpMessages){
                    stringBuilder.append(msg);
                    stringBuilder.append(", ");
                }
                cart.addMessage(stringBuilder.toString());
            }
        }
        return cart;
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
