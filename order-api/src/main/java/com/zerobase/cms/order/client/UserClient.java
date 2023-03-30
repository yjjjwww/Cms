package com.zerobase.cms.order.client;

import com.zerobase.cms.order.client.mailgun.SendMailForm;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-api", url = "${feign.client.url.user-api}")
public interface UserClient {

    @GetMapping("/customer/getInfo")
    ResponseEntity<CustomerDto> getCustomerInfo(@RequestHeader(name = "X-Auth-Token") String token);

    @PostMapping("/customer/balance")
    ResponseEntity<?> changeBalance(@RequestHeader(name = "X-Auth-Token") String token,
        @RequestBody ChangeBalanceForm form);

    @PostMapping(value = "/email", produces = "application/json")
    ResponseEntity<String> sendEmail(@RequestHeader(name = "X-Auth-Token") String token,
        @RequestBody SendMailForm form);
}
