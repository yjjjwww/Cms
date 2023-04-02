package com.zerobase.cms.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInForm {
    private String email;
    private String password;
}
