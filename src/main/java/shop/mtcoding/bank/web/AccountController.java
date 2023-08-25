package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.service.AccountService;
import shop.mtcoding.bank.service.AccountService.AccountListResponseDto;

import javax.validation.Valid;

import static shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

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
}
