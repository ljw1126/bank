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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
        JoinRequestDto joinRequestDto = getJoinRequestDto("love", "1234", "love@gmail.com", "러브");


        //when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequestDto))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("회원가입 성공"));
    }

    @DisplayName("중복아이디가 있을 경우 bad request를 반환한다")
    @Test
    void join_test_when_exist_duplicate_id() throws Exception {
        //given
        JoinRequestDto joinRequestDto = getJoinRequestDto("aaa", "1234", "aaa@gmail.com", "이름없음");

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequestDto))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("동일한 username이 존재합니다"));
    }

    @DisplayName("username에 한글은 안된다")
    @Test
    void join_by_invalid_username() throws Exception {
        //given
        JoinRequestDto joinRequestDto = getJoinRequestDto("한글이름안됨", "1234", "empty@gmail.com", "이름없음");

        //when
        //then
        mockMvc.perform(post("/api/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequestDto))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("유효성 검사 실패"))
                .andExpect(jsonPath("$.data.username").value("영문/숫자 2~20자 이내로 작성해주세요"));
    }

    @DisplayName("비밀번호는 4~20 사이의 길이를 가진다")
    @Test
    void join_by_invalid_password() throws Exception {
        //given
        JoinRequestDto joinRequestDto = getJoinRequestDto("empty", "1", "empty@gmail.com", "이름없음");

        //when
        //then
        mockMvc.perform(post("/api/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequestDto))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("유효성 검사 실패"))
                .andExpect(jsonPath("$.data.password").value("size must be between 4 and 20"));
    }

    @DisplayName("유효하지 이메일 형식으로 email 입력 해야 한다")
    @Test
    void join_by_invalid_email() throws Exception {
        //given
        JoinRequestDto joinRequestDto = getJoinRequestDto("empty", "11234", "invalid@email.co.kr", "이름없음");

        //when
        //then
        mockMvc.perform(post("/api/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequestDto))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("유효성 검사 실패"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("이메일 형식으로 작성해주세요"));
    }

    @DisplayName("fullname은 한글/영문 포함 2~20자로 작성해야 한다")
    @Test
    void join_by_invalid_fullname() throws Exception {
        //given
        JoinRequestDto joinRequestDto = getJoinRequestDto("empty", "11234", "valid@email.com", "힣");

        //when
        //then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequestDto))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("유효성 검사 실패"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fullname").value("한글/영문 2~20자 이내로 작성해 주세요"));
    }

    private static JoinRequestDto getJoinRequestDto(String username, String password, String email, String fullname) {
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername(username);
        joinRequestDto.setPassword(password);
        joinRequestDto.setEmail(email);
        joinRequestDto.setFullname(fullname);
        return joinRequestDto;
    }


}