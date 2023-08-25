package shop.mtcoding.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.List;
import java.util.Optional;

import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

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

}
