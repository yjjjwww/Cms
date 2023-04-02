package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.service.customer.EmailService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class OrderMailController {

    private final JwtAuthenticationProvider provider;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestHeader(name = "X-Auth-Token") String token,
        @RequestBody SendMailForm form) {
        return ResponseEntity.ok(
            emailService.sendEmail(provider.getUserVo(token).getId(), form).getBody());
    }
}
