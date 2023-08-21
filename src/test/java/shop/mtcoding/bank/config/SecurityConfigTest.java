package shop.mtcoding.bank.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();

        System.out.println(responseBody);
        System.out.println(httpStatusCode);

        //then

    }

    @DisplayName("")
    @Test
    void authorization() {
        //given

        //when

        //then

    }
}