package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        dateSetting();
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    // jwt token -> 인증 필터 -> 시큐리티 세션 생성
    // 디비에서 username=aaaa 조회해서 security 세션에 담아주는 어노테이션
    // java.lang.IllegalStateException: Unable to create SecurityContext using @org.springframework.security.test.context.support.WithUserDetails(setupBefore=TEST_METHOD, userDetailsServiceBeanName="", value="aaaa")
    // setupBefore = TEST_METHOD(setUp 메서드 실행전에 수행됨)
    @WithUserDetails(value = "ssar", setupBefore = TEST_EXECUTION)
    @DisplayName("")
    @Test
    void saveAccount() throws Exception {
        //given
        AccountSaveRequestDto requestDto = new AccountSaveRequestDto();
        requestDto.setNumber(9999L);
        requestDto.setPassword(1234L);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/s/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("")
    @WithUserDetails(value = "ssar", setupBefore = TEST_EXECUTION)
    @Test
    void findUserAccount() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/account/login-user"));

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.msg").value("계좌 목록"))
                .andExpect(jsonPath("$.data.fullname").value("쌀"))
                .andExpect(jsonPath("$.data.accounts", hasSize(2)))
                .andExpect(jsonPath("$.data.accounts.length()", is(2)));
    }


    private void dateSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newMockAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newMockAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newMockAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newMockAccount(4444L, ssar));
    }
}