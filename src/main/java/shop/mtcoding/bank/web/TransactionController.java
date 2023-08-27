package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.service.TransactionService;

import static shop.mtcoding.bank.dto.transaction.TransactionResponseDto.TransactionListResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<ResponseDto> findTransactionList(@PathVariable Long number,
                                                           @RequestParam(value = "gubun", defaultValue = "ALL") String gubun,
                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @AuthenticationPrincipal LoginUser loginUser
    ) {
        TransactionListResponseDto responseDto
                = transactionService.findTransactionList(loginUser.getUser().getId(), number, gubun, page);

        //ResponseEntity.ok().body(new ResponseDto(1, "일출금 목록 조회 성공", responseDto));
        return new ResponseEntity<>(new ResponseDto(1, "입출금 목록 조회 성공", responseDto), HttpStatus.OK);
    }
}
