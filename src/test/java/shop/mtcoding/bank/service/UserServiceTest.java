package shop.mtcoding.bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @DisplayName("")
    @Test
    void 회원가입_test() {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("aaa");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("aaa@gmail.com");
        joinRequestDto.setFullname("에이");

        //stub
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        User newUser = newMockUser(1L, "aaa", "에이"); // extends 해서 사용
        when(userRepository.save(any())).thenReturn(newUser);

        //when
        JoinResponseDto responseDto = userService.회원가입(joinRequestDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getUsername()).isEqualTo("aaa");
    }

    @DisplayName("중복된 username이 있는 경우 예외를 발생시킨다")
    @Test
    void 중복회원가입_test() {
        //given

        //stub
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        //when, then
        assertThatThrownBy(() -> userService.회원가입(new JoinRequestDto()))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("동일한 username이 존재합니다");
    }
}