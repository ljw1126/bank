package shop.mtcoding.bank.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.handler.CustomApiException;
import shop.mtcoding.bank.util.CustomDateUtil;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;

import static shop.mtcoding.bank.domain.transaction.TransactionEnum.*;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    public AccountListResponseDto findUserAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다"));

        // 유저가 소유한 모든 계좌 목록
        List<Account> accountList = accountRepository.findByUser_id(userId);
        return new AccountListResponseDto(user, accountList);
    }

    @Transactional
    public AccountSaveResponseDto saveAccount(AccountSaveRequestDto accountSaveRequestDto, Long userId) {
        // User 검증
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        );

        // 해당 계좌가 DB가 있는지 중복 여부 체크
        Optional<Account> accountOptional = accountRepository.findByNumber(accountSaveRequestDto.getNumber());
        if(accountOptional.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }

        // 계좌 등록
        Account account = accountRepository.save(accountSaveRequestDto.toEntity(user));

        // DTO 응답
        return new AccountSaveResponseDto(account);
    }

    @Transactional
    public void deleteAccount(Long number, Long userId) {
        // 계좌 확인
        Account account = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        );

        // 계좌 소유자 확인
        account.checkOwner(userId);

        accountRepository.deleteById(account.getId());
    }

    @Transactional
    public AccountDepositResponseDto accountDeposit(AccountDepositRequestDto requestDto) { // ATM -> 계좌 입금
        // 0원 체크
        if(requestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        // 입금 계좌 확인
        Account depositAccount = accountRepository.findByNumber(requestDto.number)
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 입금 (해당 계좌 balance 조정 - update문 - 더티체킹)
        depositAccount.deposit(requestDto.getAmount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccount)
                .depositAccountBalance(depositAccount.getBalance())
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .amount(requestDto.getAmount())
                .gubun(DEPOSIT)
                .sender("ATM")
                .receiver(requestDto.getNumber() + "")
                .tel(requestDto.getTel())
                .build();

        Transaction transactionResult  = transactionRepository.save(transaction);
        return new AccountDepositResponseDto(depositAccount, transactionResult);
    }

    @Getter
    @Setter
    public static class AccountDepositResponseDto { // ATM 입금 응답
        private Long id; // 계좌 id
        private Long number; // 계좌 번호
        private TransactionDto transactionDto;

        public AccountDepositResponseDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String reciver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance;
            private String tel;
            private String createAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositRequestDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number; // 계좌 번호

        @NotNull // 0원 유효성
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String gubun;

        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}$")
        private String tel;
    }
}
