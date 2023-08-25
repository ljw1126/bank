package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;

import static org.springframework.security.test.context.support.TestExecutionEvent.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("aaaa", "에에에에"));
    }

    // jwt token -> 인증 필터 -> 시큐리티 세션 생성
    // 디비에서 username=aaaa 조회해서 security 세션에 담아주는 어노테이션
    // java.lang.IllegalStateException: Unable to create SecurityContext using @org.springframework.security.test.context.support.WithUserDetails(setupBefore=TEST_METHOD, userDetailsServiceBeanName="", value="aaaa")
    // setupBefore = TEST_METHOD(setUp 메서드 실행전에 수행됨)
    @WithUserDetails(value = "aaaa", setupBefore = TEST_EXECUTION)
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
}