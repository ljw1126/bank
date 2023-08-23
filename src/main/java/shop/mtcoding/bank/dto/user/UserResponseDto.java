package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import static shop.mtcoding.bank.util.CustomDateUtil.*;

public class UserResponseDto {

    @Getter
    @Setter
    public static class LoginResponseDto {
        private Long id;
        private String username;
        private String createdAt;

        public LoginResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = toStringFormat(user.getCreatedAt());
        }
    }

    @Setter
    @Getter
    public static class JoinResponseDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
