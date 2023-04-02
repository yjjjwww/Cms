package com.zerobase.cms.user.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
