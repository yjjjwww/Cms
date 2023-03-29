package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.seller.SellerDto;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final JwtAuthenticationProvider provider;
    private final SellerService sellerService;

    @GetMapping("/getInfo")
    public ResponseEntity<SellerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);
        Seller s = sellerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return ResponseEntity.ok(SellerDto.from(s));
    }
}
