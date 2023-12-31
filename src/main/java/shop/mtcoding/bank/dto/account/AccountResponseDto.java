package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Getter
    @Setter
    public static class AccountListResponseDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListResponseDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Setter
        @Getter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
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
            private String tel;
            private String createAt;

            @JsonIgnore
            private Long depositAccountBalance;

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
    public static class AccountWithdrawResponseDto { // 본인계좌 - ATM 출금 응답
        private Long id; // 계좌 id
        private Long number; // 계좌 번호
        private Long balance; // 잔액
        private TransactionDto transactionDto;

        public AccountWithdrawResponseDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
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
            private String createAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountTransferResponseDto { // 계좌 이체 응답
        private Long id; // 출금 계좌 id
        private Long number; // 출금 계좌 번호
        private Long balance; // 출금 계좌 잔액
        private TransactionDto transactionDto;

        public AccountTransferResponseDto(Account account, Transaction transaction) { // 출금계좌, 거래내역
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
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
            private String createAt;

            @JsonIgnore
            private Long depositAccountBalance; // 입금 계좌 잔액

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.depositAccountBalance = transaction.getDepositAccountBalance();
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDetailResponseDto {
        private Long id; // 계좌 id
        private Long number; // 계좌 번호
        private Long balance; // 잔액
        private List<TransactionDto> transactions = new ArrayList<>(); // 내역

        public AccountDetailResponseDto(Account account, List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transactions = transactions.stream().map(t -> new TransactionDto(t, account.getNumber())).collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private Long amount;
            private String sender;
            private String reciver;
            private String tel;
            private String createdAt;
            private Long balance; // 잔액

            public TransactionDto(Transaction transaction, Long accountNumber) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.amount = transaction.getAmount();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                if (transaction.getDepositAccount() == null) { // 입금 == null 이면 출금
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null) { // 출금 == null 이면 입금
                    this.balance = transaction.getDepositAccountBalance();
                } else { // 계좌 이체
                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber()) { // 입금
                        this.balance = transaction.getDepositAccountBalance();
                    } else { // 출금
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
