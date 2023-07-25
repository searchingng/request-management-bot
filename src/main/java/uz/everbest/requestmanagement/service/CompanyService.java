package uz.everbest.requestmanagement.service;

import uz.everbest.requestmanagement.domain.entity.Company;

import java.util.List;

public interface CompanyService {

    Company save(Company company);

    Company findById(Long id);

    List<Company> getActiveCompanies();

    void delete(Long companyId);

    List<Company> getExceptsOfClient(Long clientId);
}
