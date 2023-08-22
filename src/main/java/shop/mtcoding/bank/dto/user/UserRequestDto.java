package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import javax.validation.constraints.NotEmpty;

public class UserRequestDto {
    @Setter
    @Getter
    @NoArgsConstructor
    public static class JoinRequestDto {
        @NotEmpty // null이거나 공백일 수 없다
        private String username;

        @NotEmpty
        private String password;

        @NotEmpty
        private String email;

        @NotEmpty
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
