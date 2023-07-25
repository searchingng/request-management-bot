package uz.everbest.requestmanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.everbest.requestmanagement.domain.entity.User;
import uz.everbest.requestmanagement.domain.enums.UserRole;
import uz.everbest.requestmanagement.repository.UserRepository;
import uz.everbest.requestmanagement.util.PasswordUtil;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminCreationConfig {

    private final UserRepository userRepository;

    @Bean
    public void createAdminIfNotExists() {
        List<User> userList = userRepository.findByRole(UserRole.ADMIN);
        User user;
        if (userList.isEmpty()) {
            user = userRepository.save(
                    User.builder()
                            .role(UserRole.ADMIN)
                            .fullName("Admin")
                            .password(PasswordUtil.generatePassword(UserRole.ADMIN.name().toLowerCase()))
                            .position("admin")
                            .build()
            );
        } else {
            user = userList.get(0);
        }

        log.info("Admin password: {}", user.getPassword());
    }

}
