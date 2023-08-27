package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.service.AccountService;

import javax.validation.Valid;

import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountTransferResponseDto;
import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountWithdrawResponseDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<ResponseDto> saveAccount(@RequestBody @Valid AccountSaveRequestDto accountSaveRequestDto
            , BindingResult bindingResult
            , @AuthenticationPrincipal LoginUser loginUser
    ) {
        AccountSaveResponseDto response = accountService.saveAccount(accountSaveRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", response), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
        AccountListResponseDto responseDto = accountService.findUserAccount(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 목록", responseDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser) {
        accountService.deleteAccount(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<ResponseDto> accountDeposit(@RequestBody @Valid AccountDepositRequestDto accountSaveRequestDto
            , BindingResult bindingResult
    ) {
        AccountDepositResponseDto response = accountService.accountDeposit(accountSaveRequestDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", response), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<ResponseDto> accountWithdraw(@RequestBody @Valid AccountWithdrawRequestDto withdrawRequestDto
            , BindingResult bindingResult
            , @AuthenticationPrincipal LoginUser loginUser
    ) {
        AccountWithdrawResponseDto response = accountService.accountWithdraw(withdrawRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", response), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<ResponseDto> accountTransfer(@RequestBody @Valid AccountTransferRequestDto accountTransferRequestDto
            , BindingResult bindingResult
            , @AuthenticationPrincipal LoginUser loginUser
    ) {
        AccountTransferResponseDto response = accountService.accountTransfer(accountTransferRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", response), HttpStatus.CREATED);
    }
}
