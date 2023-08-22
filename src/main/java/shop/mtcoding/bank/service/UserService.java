package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.CustomApiException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서비스는 dto로 요청받고 응답한다
    @Transactional
    public JointRespDto 회원가입(JoinReqDto joinReqDto) {
        // 1. 동일 유저 네임 존재 검사
        Optional<User> userOptional = userRepository.findByUsername(joinReqDto.getUsername());

        if(userOptional.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다");
        }

        // 2. 패스워드 인코딩 // persistence context
        User user = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JointRespDto(user);
    }

    @Getter
    public static class JointRespDto {
        private Long id;
        private String username;
        private String fullname;

        public JointRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }

    @Getter
    public static class JoinReqDto {
        private String username;
        private String password;
        private String email;
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .fullname(passwordEncoder.encode(password))
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
