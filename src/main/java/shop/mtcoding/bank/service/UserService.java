package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto;
import shop.mtcoding.bank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.Optional;

import static shop.mtcoding.bank.dto.user.UserResponseDto.*;

@Service
@RequiredArgsConstructor
public class UserService {
    Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서비스는 dto로 요청받고 응답한다
    @Transactional
    public JoinResponseDto 회원가입(JoinRequestDto joinReqDto) {
        // 1. 동일 유저 네임 존재 검사
        Optional<User> userOptional = userRepository.findByUsername(joinReqDto.getUsername());

        if(userOptional.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다");
        }

        // 2. 패스워드 인코딩 // persistence context
        User user = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JoinResponseDto(user);
    }

}
