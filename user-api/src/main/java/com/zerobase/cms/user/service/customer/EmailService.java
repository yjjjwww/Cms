package com.zerobase.cms.user.service.customer;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailgunClient mailgunClient;
    private final CustomerRepository customerRepository;

    public ResponseEntity<String> sendEmail(Long id, SendMailForm form) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(!customer.getEmail().equals(form.getTo())){
            throw new CustomException(ErrorCode.WRONG_EMAIL_ADDRESS);
        }

        return mailgunClient.sendEmail(form);
    }
}
