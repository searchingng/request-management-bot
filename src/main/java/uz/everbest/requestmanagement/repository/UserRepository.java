package uz.everbest.requestmanagement.repository;

import org.springframework.data.repository.CrudRepository;
import uz.everbest.requestmanagement.domain.entity.User;
import uz.everbest.requestmanagement.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByChatId(Long chatId);

    Optional<User> findByPassword(String password);

    List<User> findByRole(UserRole role);
}
