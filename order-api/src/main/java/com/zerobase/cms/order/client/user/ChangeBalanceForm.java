package com.zerobase.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
