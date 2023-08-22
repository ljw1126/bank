package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRequestDto {
    @Setter
    @Getter
    @NoArgsConstructor
    public static class JoinRequestDto {
        @NotEmpty // null이거나 공백일 수 없다
        @Pattern(regexp = "^[0-9a-zA-Z]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
        private String username;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}()$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣0-9a-zA-Z]{2,20}$", message = "한글/영문 2~20자 이내로 작성해 주세요")
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
