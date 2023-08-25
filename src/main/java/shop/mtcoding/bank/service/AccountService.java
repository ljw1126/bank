package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.CustomApiException;

import javax.validation.constraints.Digits;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaverResponseDto accountRegistration(AccountSaveRequestDto accountSaveRequestDto, Long userId) {
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
        return new AccountSaverResponseDto(account);
    }

    @Getter
    @Setter
    public static class AccountSaverResponseDto {
        private Long id;
        private Long number;
        private Long balance; // 잔액

        public AccountSaverResponseDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getNumber();
        }
    }

    @Getter
    public static class AccountSaveRequestDto {
        @NonNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NonNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L) // 잔액
                    .user(user)
                    .build();
        }
    }
}
