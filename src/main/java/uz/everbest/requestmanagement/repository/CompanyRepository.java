package uz.everbest.requestmanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.everbest.requestmanagement.domain.entity.Company;
import uz.everbest.requestmanagement.domain.enums.CompanyStatus;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends CrudRepository<Company, Long> {

    List<Company> findByStatus(CompanyStatus status);

    @Query("SELECT c FROM Company c WHERE c.status = 'ACTIVE' AND c.id NOT IN (SELECT cl.companyId FROM Client cl WHERE cl.userId = ?1)")
    List<Company> findExceptsOfClient(Long clientId);

}
