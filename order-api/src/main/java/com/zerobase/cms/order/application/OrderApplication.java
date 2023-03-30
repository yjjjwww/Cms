package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.mailgun.SendMailForm;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.ProductItemService;
import java.time.LocalDate;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderApplication {

    private final CartApplication cartApplication;

    private final UserClient userClient;
    private final ProductItemService productItemService;

    @Transactional
    public Cart order(String token, Cart cart) {
        Cart orderCart = cartApplication.refreshCart(cart);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ErrorCode.ORDER_FAIL_CHECK_CART);
        }

        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(orderCart);
        if (customerDto.getBalance() < totalPrice) {
            throw new CustomException(ErrorCode.ORDER_FAIL_NOT_ENOUGH_BALANCE);
        }

        userClient.changeBalance(token, new ChangeBalanceForm("USER", "Order", -totalPrice));

        for(Cart.Product product : orderCart.getProducts()){
            for(Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }

        return orderCart;
    }

    public String sendOrderMail(String token, Cart cart){
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();
        SendMailForm form = SendMailForm.builder()
            .from("yjjjwww@naver.com")
            .to(customerDto.getEmail())
            .subject(customerDto.getEmail() + "님의 " + LocalDate.now().toString() + "일자 주문 확인 메일입니다")
            .text(cart.toString())
            .build();
        userClient.sendEmail(token, form);
        return cart.toString();
    }

    private Integer getTotalPrice(Cart cart) {
        return cart.getProducts().stream().flatMapToInt(
                product -> product.getItems().stream().flatMapToInt(
                    productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())))
            .sum();
    }
}