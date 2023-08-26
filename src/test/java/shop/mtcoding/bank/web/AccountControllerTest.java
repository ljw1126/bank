package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.handler.CustomApiException;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;

@Sql("classpath:/db/teardown.sql")
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

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        dateSetting();
        em.clear(); // 영속성 컨텍스트 비움
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

    @DisplayName("")
    @WithUserDetails(value = "ssar", setupBefore = TEST_EXECUTION)
    @Test
    void deleteAccount() throws Exception {
        //given
        Long number = 1111L;

        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/s/account/" + number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then
        //JUnit 테스트에서 delete 쿼리는 DB관련(DML)으로 가장 마지막에 실행되면 발동안함
        Assertions.assertThatThrownBy(() -> accountRepository.findByNumber(number).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다")))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("계좌를 찾을 수 없습니다");
    }

    /**
     * 테스트 시에 insert한 데이터는 전부 Persistence Context에 올라감 (영속화)
     * 영속화 초기화 해주는 것이 개발 모드와 동일한 환경으로 테스트를 할 수 있게 해준다.
     * Lazy 로딩은 P.C에 있다면 쿼리 발생하지 않고, 없다면 쿼리 발생한다
     */
    @DisplayName("")
    @WithUserDetails(value = "cos", setupBefore = TEST_EXECUTION)
    @Test
    void deleteAccountWhenNotOwnAccount() throws Exception {
        //given
        Long number = 1111L; // ssar account

        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/s/account/" + number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then

    }

    @Nested
    @DisplayName("계좌 입금 테스트")
    @TestMethodOrder(MethodOrderer.MethodName.class)
    class AccountDeposit {
        @DisplayName("ATM -> 계좌입금 성공 테스트")
        @Test
        @Order(1)
        void successAccountDeposit() throws Exception {
            //given
            AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
            accountDepositRequestDto.setNumber(1111L);
            accountDepositRequestDto.setAmount(1000L);
            accountDepositRequestDto.setGubun("DEPOSIT");
            accountDepositRequestDto.setTel("01012345678");

            //when
            ResultActions resultActions = mockMvc.perform(post("/api/account/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountDepositRequestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("1"))
                    .andExpect(jsonPath("$.msg").value("계좌 입금 완료"))
                    .andExpect(jsonPath("$.data.number").value("1111"));

        }

        @DisplayName("0원 이하의 금액을 입금할 수 없습니다")
        @Test
        @Order(2)
        void unSuccessAccountDeposit1() throws Exception {
            //given
            AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
            accountDepositRequestDto.setNumber(1111L);
            accountDepositRequestDto.setAmount(0L);
            accountDepositRequestDto.setGubun("DEPOSIT");
            accountDepositRequestDto.setTel("01012345678");

            //when
            //then
            mockMvc.perform(post("/api/account/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(accountDepositRequestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("-1"))
                    .andExpect(jsonPath("$.msg").value("0원 이하의 금액을 입금할 수 없습니다"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("계좌를 찾을 수 없습니다")
        @Test
        @Order(3)
        void unSuccessAccountDeposit2() throws Exception {
            //given
            AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
            accountDepositRequestDto.setNumber(9999L);
            accountDepositRequestDto.setAmount(1000L);
            accountDepositRequestDto.setGubun("DEPOSIT");
            accountDepositRequestDto.setTel("01012345678");

            //when
            //then
            mockMvc.perform(post("/api/account/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(accountDepositRequestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("-1"))
                    .andExpect(jsonPath("$.msg").value("계좌를 찾을 수 없습니다"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("gubun은 DEPOSIT만 가능합니다")
        @Test
        @Order(4)
        void unSuccessAccountDeposit3() throws Exception {
            //given
            AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
            accountDepositRequestDto.setNumber(1111L);
            accountDepositRequestDto.setAmount(1000L);
            accountDepositRequestDto.setGubun("WITHDROW");
            accountDepositRequestDto.setTel("01012345678");

            //when
            //then
            mockMvc.perform(post("/api/account/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(accountDepositRequestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("-1"))
                    .andExpect(jsonPath("$.msg").value("유효성 검사 실패"));
        }

        @DisplayName("tel은 11자리 숫자여야 합니다")
        @Test
        @Order(5)
        void unSuccessAccountDeposit4() throws Exception {
            //given
            AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
            accountDepositRequestDto.setNumber(1111L);
            accountDepositRequestDto.setAmount(1000L);
            accountDepositRequestDto.setGubun("DEPOSIT");
            accountDepositRequestDto.setTel("0101234567899999999");

            //when
            //then
            mockMvc.perform(post("/api/account/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(accountDepositRequestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("-1"))
                    .andExpect(jsonPath("$.msg").value("유효성 검사 실패"));

        }
    }
}