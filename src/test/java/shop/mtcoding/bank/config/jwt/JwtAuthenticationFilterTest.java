package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.user.UserRequestDto.LoginRequestDto;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 가짜 환경에 컴포넌트 스캔해서 주입
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("aaaa", "1234"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch(); // @Transactional 클래스에 붙여서 rollback 해도 된다
    }

    @DisplayName("로그인 성공할 경우 jwt token 을 header에 담아 응답한다")
    @Test
    void successfulAuthentication() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("aaaa");
        loginRequestDto.setPassword("1234");
        String requestBody = objectMapper.writeValueAsString(loginRequestDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        //then
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);

        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken.startsWith(JwtVO.TOKEN_PREFIX)).isTrue();

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.msg").value("로그인 성공"))
                .andExpect(jsonPath("$.data.username").value("aaaa"));
    }

    @DisplayName("로그인 실패시 401 에러가 출력된다")
    @Test
    void unsuccessfulAuthentication() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("aaaa");
        loginRequestDto.setPassword("failPassword");
        String requestBody = objectMapper.writeValueAsString(loginRequestDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        //then
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);

        assertThat(jwtToken).isNull();

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("로그인 실패"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}