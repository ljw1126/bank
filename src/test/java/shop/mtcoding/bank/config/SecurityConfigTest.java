package shop.mtcoding.bank.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc // Mock 환경에 MockMvc가 등록
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 가짜 환경
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("")
    @Test
    void authentication() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/s/hello")) // MockMvcRequestBuilders
                .andDo(print()) // MockMvcResultHandlers
                .andExpect(status().isUnauthorized()) // MockMvcResultMatchers
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.msg").value("로그인을 진행해 주세요"));
    }

    @DisplayName("")
    @Test
    void authorization() throws Exception {
        //given

        //when

        //then
    }
}