package uz.everbest.requestmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.everbest.requestmanagement.domain.entity.User;
import uz.everbest.requestmanagement.domain.enums.UserRole;
import uz.everbest.requestmanagement.repository.UserRepository;
import uz.everbest.requestmanagement.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId)
                .orElse(User.builder().chatId(chatId).build());
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(new User());
    }

    @Override
    public User findByPassword(String password) {
        return userRepository.findByPassword(password)
                .orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User authorize(User user, User account) {
        account.setChatId(user.getChatId());
        account.setLastAction("loggedin");
        account.setLangCode(user.getLangCode());
        userRepository.delete(user);
        return save(account);
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User logout(User user) {
        var newUser =  User.builder()
                .chatId(user.getChatId())
                .langCode(user.getLangCode())
                .lastAction("loggedout")
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .build();

        user.setChatId(null);
        user.setLastAction("loggedout");
        save(user);

        return userRepository.save(newUser);
    }

    @Override
    public void deactive(Long userId) {
        User user = findById(userId);
        user.setChatId(null);
        user.setPassword(null);
        user.setPosition("OLD-" + user.getRole());
        user.setRole(null);
        user.setLastAction("loggedout");
        save(user);
    }
}
