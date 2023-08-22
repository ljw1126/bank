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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static shop.mtcoding.bank.dto.user.UserRequestDto.JoinRequestDto;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(newUser("aaa", "에이"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입 성공")
    @Test
    void join_test() throws Exception {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("love");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("love@gmail.com");
        joinRequestDto.setFullname("러브");


        //when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequestDto))
        );

        //then
        resultActions.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("회원가입 성공"));
    }

    @DisplayName("중복아이디가 있을 경우 bad request를 반환한다")
    @Test
    void join_test_when_exist_duplicate_id() throws Exception {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("aaa");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("aaa@gmail.com");
        joinRequestDto.setFullname("에이");

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequestDto))
        );

        //then
        resultActions.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("동일한 username이 존재합니다"));
    }

}