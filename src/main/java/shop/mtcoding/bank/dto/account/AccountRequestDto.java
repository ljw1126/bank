package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import javax.validation.constraints.Digits;

public class AccountRequestDto {
    @Getter
    @Setter
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
