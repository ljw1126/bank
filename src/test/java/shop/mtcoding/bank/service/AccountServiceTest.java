package shop.mtcoding.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

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

    @Nested
    @DisplayName("신규 계좌 생성시 예외 케이스 테스트")
    @TestMethodOrder(MethodOrderer.MethodName.class)
    class SaveAccountException {

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

        @DisplayName("다른 유저가 본인 명의 아닌 계좌 삭제 시도할 경우 에러가 발생한다")
        @Test
        void deleteAccountWhenNotEqualsUserId() throws Exception {
            //given
            Long number = 1111L;
            Long userId = 2L;

            //stub
            User user = newMockUser(1L, "ssar", "쌀");
            Account ssarAccount = newMockAccount(1L, 111L, 1000L, user);
            when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

            //when
            //then
            assertThatThrownBy(() -> accountService.deleteAccount(number, userId))
                    .isInstanceOf(CustomApiException.class)
                    .hasMessage("계좌 소유자가 아닙니다");
        }
    }

    // (1) Account -> balance 변경되었는지 확인
    // (2) Transaction -> balance 잘 기록 되었는지
    @DisplayName("계좌 입금 서비스")
    @Test
    void accountDeposit() throws Exception {
        //given
        AccountDepositRequestDto requestDto = new AccountDepositRequestDto();
        requestDto.setNumber(1111L);
        requestDto.setAmount(100L);
        requestDto.setGubun("DEPOSIT");
        requestDto.setTel("01012345678");

        //stub1
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar); // 실행됨 -- 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount)); // 실행 안됨

        //stub2
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar);
        ;
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2); // 실행됨 -- 1100원
        when(transactionRepository.save(any())).thenReturn(transaction); // 실행안됨

        //when
        AccountDepositResponseDto responseDto = accountService.accountDeposit(requestDto);
        System.out.println(objectMapper.writeValueAsString(responseDto));

        //then
        assertThat(responseDto.getTransactionDto().getDepositAccountBalance()).isEqualTo(1100L);
        assertThat(ssarAccount.getBalance()).isEqualTo(1100L);
    }

    @DisplayName("0원은 입금할 수 없다")
    @Test
    void accountDeposit2() throws Exception {
        //given
        AccountDepositRequestDto requestDto = new AccountDepositRequestDto();
        requestDto.setNumber(1111L);
        requestDto.setAmount(0L);
        requestDto.setGubun("DEPOSIT");
        requestDto.setTel("01012345678");

        //when
        //then
        assertThatThrownBy(() -> accountService.accountDeposit(requestDto))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("0원 이하의 금액을 입금할 수 없습니다");
    }

    // 서비스 테스트시, 내가 지금 무엇을 여기서 테스트 해야 할지 명확히 구분 필요 (책임 분리)
    // DTO를 만드는 책임 -> 서비스에 있지만, 검증은 Controller 에서 하도록 책임 분리 가능
    // DB 관련 부분도 서비스 책임이 아니므로 할 필요 없음
    // **DB 관련 된 것을 조회했을 때, 그 값을 통해 어떤 비지니스 로직이 흘러 가는 경우 stub 정의해서 테스트 해보면 된다
    // 하지만 간단한 비즈니스 로직 검증의 경우 아래와 같이 하는 것이 더 나을 수 있다.
    @DisplayName("")
    @Test
    void accountDeposit3() throws Exception {
        //given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        //when
        if (amount <= 0L) {
            throw new CustomApiException("0원 히아의 금액을 입금할 수 없습니다");
        }

        account.deposit(amount);

        //then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }


    // TODO. 계좌 출금

    // TODO. 계좌 이체

    // TODO. 계좌 목록보기 (유저별 테스트)

    // TODO. 계좌 상세보기 테스트
}