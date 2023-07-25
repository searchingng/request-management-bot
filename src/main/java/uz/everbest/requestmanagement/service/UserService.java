package uz.everbest.requestmanagement.service;

import uz.everbest.requestmanagement.domain.entity.User;
import uz.everbest.requestmanagement.domain.enums.UserRole;

import java.util.List;

public interface UserService {

    User findByChatId(Long chatId);

    User findById(Long id);

    User findByPassword(String password);

    User save(User user);

    User authorize(User user, User account);

    List<User> findByRole(UserRole role);

    User logout(User user);

    void deactive(Long userId);
}
