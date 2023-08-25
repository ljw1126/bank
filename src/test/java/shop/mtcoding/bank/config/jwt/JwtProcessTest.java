package shop.mtcoding.bank.config.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtProcessTest {

    private String createToken() {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);

        //when
        String jwtToken = JwtProcess.create(loginUser);
        return jwtToken;
    }

    @DisplayName("")
    @Test
    void create() {
        //given

        //when
        String jwtToken = createToken();
        System.out.println(jwtToken);

        //then
        assertThat(jwtToken.startsWith(JwtVO.TOKEN_PREFIX)).isTrue();
    }

    @DisplayName("")
    @Test
    void verify() {
        //given
        String token = createToken();
        String jwtToken = token.replace(JwtVO.TOKEN_PREFIX, "");

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);

        //then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.CUSTOMER);
    }
}