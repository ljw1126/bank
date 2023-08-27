package shop.mtcoding.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.dto.transaction.TransactionResponseDto.TransactionListResponseDto;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListResponseDto findTransactionList(Long userId, Long accountNumber, String gubun, int page) {
        // 계좌 확인
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("해당 계좌를 찾을 수 없습니다"));

        // 계좌 소유주 확인
        account.checkOwner(userId);

        List<Transaction> transactionList = transactionRepository.findTransactionList(account.getId(), gubun, page);
        return new TransactionListResponseDto(transactionList, account);
    }
}
