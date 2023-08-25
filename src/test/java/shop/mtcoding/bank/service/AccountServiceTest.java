package shop.mtcoding.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Spy // 진짜 객체를 InjectMocks 주입한다
    private ObjectMapper objectMapper;

    @DisplayName("신규 계좌를 생성한다")
    @Test
    void saveAccount() throws JsonProcessingException {
        //given
        Long userId = 1L;

        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
        accountSaveRequestDto.setNumber(1111L); // 계좌 번호
        accountSaveRequestDto.setPassword(1234L);

        //stub1
        User dummyUser = newMockUser(1L, "aaaa", "에에에에");
        when(userRepository.findById(any())).thenReturn(Optional.of(dummyUser));

        //stub2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        //stub3
        Account mockAccount = newMockAccount(1L, 1111L, 1000L, dummyUser);
        when(accountRepository.save(any())).thenReturn(mockAccount);

        //when
        AccountSaveResponseDto responseDto = accountService.saveAccount(accountSaveRequestDto, userId);
        String responseBody = objectMapper.writeValueAsString(responseDto);
        System.out.println(responseBody);

        //then
        assertThat(responseDto.getNumber()).isEqualTo(1111L);
    }

    @DisplayName("신규 계좌 등록시 해당되는 userId가 없는 경우 에러를 출력한다")
    @Test
    void saveAccountWhenUnAuthenticationUserId() throws JsonProcessingException {
        long userId = 9999L;

        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
        accountSaveRequestDto.setNumber(1111L); // 계좌 번호
        accountSaveRequestDto.setPassword(1234L);

        assertThatThrownBy(() -> accountService.saveAccount(accountSaveRequestDto, userId))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }

    @DisplayName("신규 계좌 등록시 중복 계좌 번호가 있으면 에러를 출력한다")
    @Test
    void saveAccountWhenDuplicatedAccount() throws JsonProcessingException {
        long userId = 1L;
        long accountNumber = 1111L;

        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
        accountSaveRequestDto.setNumber(accountNumber); // 계좌 번호
        accountSaveRequestDto.setPassword(1234L);

        //stub
        User mockUser = newMockUser(1L, "aaaa", "에에에에");
        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

        //stub
        Account dummyAccount = newMockAccount(accountNumber, mockUser);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(dummyAccount));


        assertThatThrownBy(() -> accountService.saveAccount(accountSaveRequestDto, userId))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("해당 계좌가 이미 존재합니다");
    }
}