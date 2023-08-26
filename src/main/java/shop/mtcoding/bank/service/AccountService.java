package shop.mtcoding.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.List;
import java.util.Optional;

import static shop.mtcoding.bank.domain.transaction.TransactionEnum.DEPOSIT;
import static shop.mtcoding.bank.domain.transaction.TransactionEnum.WITHDRAW;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountWithdrawResponseDto;

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
        Account depositAccount = accountRepository.findByNumber(requestDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 입금 (해당 계좌 balance 조정 - update문 - 더티체킹)
        depositAccount.deposit(requestDto.getAmount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccount)
                .depositAccountBalance(depositAccount.getBalance())
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .amount(requestDto.getAmount()) // 입금 금액
                .gubun(DEPOSIT)
                .sender("ATM")
                .receiver(requestDto.getNumber() + "")
                .tel(requestDto.getTel())
                .build();

        Transaction transactionResult  = transactionRepository.save(transaction);
        return new AccountDepositResponseDto(depositAccount, transactionResult);
    }

    @Transactional
    public AccountWithdrawResponseDto accountWithdraw(AccountWithdrawRequestDto accountWithdrawRequestDto, Long userId) {
        // 0원 체크
        if(accountWithdrawRequestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        // 출금 계좌 확인
        Account withdrawAccount = accountRepository.findByNumber(accountWithdrawRequestDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 출금 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccount.checkOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccount.checkSamePassword(accountWithdrawRequestDto.getPassword());

        // 출금계좌 잔액 확인
        withdrawAccount.checkBalance(accountWithdrawRequestDto.getAmount());

        // 출금하기
        withdrawAccount.withdraw(accountWithdrawRequestDto.getAmount());

        // 거래내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .depositAccount(null)
                .depositAccountBalance(null)
                .amount(accountWithdrawRequestDto.getAmount()) // 출금 요청 금액
                .gubun(WITHDRAW)
                .sender(accountWithdrawRequestDto.getNumber() + "")
                .receiver("ATM")
                .build();

        Transaction transactionResult  = transactionRepository.save(transaction);

        // DTO 응답
        return new AccountWithdrawResponseDto(withdrawAccount, transactionResult);
    }

}
