package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;

public class AccountResponseDto {
    @Getter
    @Setter
    public static class AccountSaveResponseDto {
        private Long id;
        private Long number;
        private Long balance; // 잔액

        public AccountSaveResponseDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }
}
