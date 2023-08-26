package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

    @Getter
    @Setter
    public static class AccountWithdrawRequestDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number; // 계좌 번호

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        @NotNull // 0원 유효성
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(WITHDRAW)$")
        private String gubun;

    }
}
