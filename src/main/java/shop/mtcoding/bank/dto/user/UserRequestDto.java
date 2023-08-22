package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class UserRequestDto {
    @Setter
    @Getter
    public static class JoinRequestDto {
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
