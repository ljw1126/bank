package shop.mtcoding.bank.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.CustomApiException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static shop.mtcoding.bank.service.UserService.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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

        User user = User.builder()
                .id(1L)
                .username("aaa")
                .password("1234")
                .email("aaa@gmail.com")
                .fullname("에이")
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.save(any())).thenReturn(user);

        //when
        JoinResponseDto responseDto = userService.회원가입(joinRequestDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getUsername()).isEqualTo("aaa");
    }

    @DisplayName("")
    @Test
    void 중복회원가입_test() {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("aaa");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("aaa@gmail.com");
        joinRequestDto.setFullname("에이");

        //stub
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User())); // 에러 발생

        //when, then
        assertThatThrownBy(() -> userService.회원가입(joinRequestDto))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("동일한 username이 존재합니다");
    }
}