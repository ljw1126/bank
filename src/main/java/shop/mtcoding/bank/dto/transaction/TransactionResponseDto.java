package shop.mtcoding.bank.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionResponseDto {
    @Getter
    @Setter
    public static class TransactionListResponseDto {
        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionListResponseDto(List<Transaction> transactionList, Account account) {
            this.transactions = transactionList.stream()
                    .map(t -> new TransactionDto(t, account.getNumber()))
                    .collect(Collectors.toList());
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

                if(transaction.getDepositAccount() == null) { // 입금 == null 이면 출금
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if(transaction.getWithdrawAccount() == null) { // 출금 == null 이면 입금
                    this.balance = transaction.getDepositAccountBalance();
                } else { // 계좌 이체
                    if(accountNumber.longValue() == transaction.getDepositAccount().getNumber()) { // 입금
                        this.balance = transaction.getDepositAccountBalance();
                    } else { // 출금
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
