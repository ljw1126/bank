package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.user.User;

public class UserResponseDto {
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
