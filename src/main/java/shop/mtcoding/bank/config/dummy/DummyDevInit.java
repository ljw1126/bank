package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject{

    @Profile("dev") // dev profile에서만 무조건 실행
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return (args) -> {
            User user = userRepository.save(newUser("aaaa", "1234"));

        };
    }
}
