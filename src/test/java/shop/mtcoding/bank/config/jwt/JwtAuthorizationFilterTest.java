package shop.mtcoding.bank.config.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 로그인시 모든 경로에 대한 권한 체크 필터
@Sql("classpath:/db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("header 에 jwtToken 이 존재할 경우 404 not found 응답을 한다(페이지가 없으므로)")
    @Test
    void authorization_success_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello/test")
                .header(JwtVO.HEADER, jwtToken)
        );

        //then
        resultActions.andDo(print())
                     .andExpect(status().isNotFound());
    }

    @DisplayName("")
    @Test
    void authorization_fail_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello/test"));

        //then
        resultActions.andDo(print())
                .andExpect(status().isUnauthorized()); // 접근 권한 없음
    }

    @DisplayName("CUTOMER 권한으로 admin 주소 접근시 forbidden 응답한다")
    @Test
    void authorization_admin_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin")
                .header(JwtVO.HEADER, jwtToken)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden());
    }
}