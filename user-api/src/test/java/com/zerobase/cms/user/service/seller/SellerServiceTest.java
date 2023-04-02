package com.zerobase.cms.user.service.seller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    @Test
    void signUp() {
        SignUpForm form = SignUpForm.builder()
            .name("name")
            .birth(LocalDate.now())
            .email("abc@gmail.com")
            .password("1")
            .phone("01000000000")
            .build();

        given(sellerRepository.save(any()))
            .willReturn(Seller.from(form));

        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);

        Seller seller = sellerService.signUp(form);

        verify(sellerRepository, times(1)).save(captor.capture());
        assertEquals("name", seller.getName());
    }

    @Test
    void isEmailExist() {
        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(Seller.builder().build()));

        boolean result = sellerService.isEmailExist("email");

        assertEquals(true, result);
    }

    @Test
    void isEmailNotExist() {
        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        boolean result = sellerService.isEmailExist("email");

        assertEquals(false, result);
    }

    @Test
    void verifyEmailSuccess() {
        Seller sellerBeforeVerify = Seller.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(sellerBeforeVerify));

        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);

        sellerService.verifyEmail("zerobase@naver.com", "0000");

        verify(sellerRepository, times(1)).save(captor.capture());
    }

    @Test
    void verifyEmailFail_NOT_FOUND_USER() {
        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> sellerService.verifyEmail("zerobase", "1111"));

        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
        assertEquals("일치하는 회원이 없습니다.", exception.getMessage());
    }

    @Test
    void verifyEmailFail_ALREADY_VERIFY() {
        Seller sellerBeforeVerify = Seller.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode("0000")
            .verify(true)
            .build();

        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(sellerBeforeVerify));

        CustomException exception = assertThrows(CustomException.class,
            () -> sellerService.verifyEmail("zerobase", "1111"));

        assertEquals(ErrorCode.ALREADY_VERIFY, exception.getErrorCode());
        assertEquals("이미 인증이 완료되었습니다.", exception.getMessage());
    }

    @Test
    void verifyEmailFail_WRONG_VERIFICATION() {
        Seller sellerBeforeVerify = Seller.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(sellerBeforeVerify));

        CustomException exception = assertThrows(CustomException.class,
            () -> sellerService.verifyEmail("zerobase", "1111"));

        assertEquals(ErrorCode.WRONG_VERIFICATION, exception.getErrorCode());
        assertEquals("잘못된 인증 시도입니다.", exception.getMessage());
    }

    @Test
    void verifyEmailFail_EXPIRE_CODE() {
        Seller sellerBeforeVerify = Seller.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().minusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.ofNullable(sellerBeforeVerify));

        CustomException exception = assertThrows(CustomException.class,
            () -> sellerService.verifyEmail("zerobase", "0000"));

        assertEquals(ErrorCode.EXPIRE_CODE, exception.getErrorCode());
        assertEquals("인증 시간이 만료되었습니다.", exception.getMessage());
    }

    @Test
    void changeCustomerValidateEmail() {
        Seller customer = Seller.builder()
            .email("zerobase@naver.com")
            .name("zerobase")
            .password("zero1234@!#")
            .phone("01011112222")
            .birth(LocalDate.now())
            .verifyExpiredAt(LocalDateTime.now().minusDays(1))
            .verificationCode("0000")
            .verify(false)
            .build();

        given(sellerRepository.findById(anyLong()))
            .willReturn(Optional.ofNullable(customer));

        LocalDateTime result = sellerService.changeSellerValidateEmail(1L, "0000");

        assertEquals(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), result.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    void findByIdAndEmail() {
        given(sellerRepository.findById(anyLong()).stream().filter(
            seller -> seller.getEmail().equals(anyString())).findFirst())
            .willReturn(Optional.of(Seller.builder()
                .email("zerobase@naver.com")
                .name("zerobase")
                .password("1234")
                .build()));

        Seller seller = sellerService.findByIdAndEmail(1L, "zerobase@naver.com").get();

        assertEquals("zerobase@naver.com", seller.getEmail());
        assertEquals("zerobase", seller.getName());
        assertEquals("1234", seller.getPassword());
    }

    @Test
    void findValidSeller() {
        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Seller.builder()
                .email("zerobase@naver.com")
                .name("zerobase")
                .password("1234")
                .verify(true)
                .build()));

        Seller seller = sellerService.findValidSeller( "zerobase@naver.com", "1234").get();

        assertEquals("zerobase@naver.com", seller.getEmail());
        assertEquals("zerobase", seller.getName());
        assertEquals("1234", seller.getPassword());
    }

    @Test
    void findValidSeller_Not_Verify() {
        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Seller.builder()
                .email("zerobase@naver.com")
                .name("zerobase")
                .password("1234")
                .verify(false)
                .build()));

        Optional<Seller> customer = sellerService.findValidSeller( "zerobase@naver.com", "1234");

        assertEquals(false, customer.isPresent());
    }

    @Test
    void findValidSeller_No_Email() {
        given(sellerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Seller.builder()
                .email("zerobase@gmail.com")
                .name("zerobase")
                .password("1234")
                .verify(false)
                .build()));

        Optional<Seller> seller = sellerService.findValidSeller( "zerobase@naver.com", "1234");

        assertEquals(false, seller.isPresent());
    }
}