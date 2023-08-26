package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

import static shop.mtcoding.bank.domain.transaction.TransactionEnum.DEPOSIT;

public class DummyObject {
    protected User newUser(String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");

        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");

        LocalDateTime now = LocalDateTime.now();

        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    protected Account newMockAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // 계좌 1111L , 1000원이 있을떄
    // 입금 트랜잭션 -> 계좌 1100원 변경 -> 입금 트랜잭션 히스토리가 생성되어야 함
    protected static Transaction newMockDepositTransaction(Long id, Account account) {
        LocalDateTime now = LocalDateTime.now();
        account.deposit(100L); // 금액 추가

        return Transaction.builder()
                .id(id)
                .depositAccount(account)
                .depositAccountBalance(account.getBalance())
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .amount(100L)
                .gubun(DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01012345678")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
