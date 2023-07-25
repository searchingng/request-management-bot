package uz.everbest.requestmanagement.repository;

import org.springframework.data.repository.CrudRepository;
import uz.everbest.requestmanagement.domain.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {

    List<Client> findByUserIdAndIsActiveIsTrue(Long userId);

    Integer countByCompanyId(Long companyId);

    List<Client> findByCompanyIdAndIsActiveIsTrue(Long companyId);

    List<Client> findByIsActiveIsTrue();

}
