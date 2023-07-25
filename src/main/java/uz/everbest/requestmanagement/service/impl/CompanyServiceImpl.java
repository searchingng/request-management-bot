package uz.everbest.requestmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.everbest.requestmanagement.domain.entity.Company;
import uz.everbest.requestmanagement.domain.enums.CompanyStatus;
import uz.everbest.requestmanagement.repository.CompanyRepository;
import uz.everbest.requestmanagement.service.CompanyService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(new Company());
    }

    @Override
    public List<Company> getActiveCompanies() {
        return companyRepository.findByStatus(CompanyStatus.ACTIVE);
    }

    @Override
    public void delete(Long companyId) {
        Company company = findById(companyId);
        if (company == null)
            return;

        company.setStatus(CompanyStatus.DELETED);
        save(company);
    }

    @Override
    public List<Company> getExceptsOfClient(Long clientId) {
        return companyRepository.findExceptsOfClient(clientId);
    }
}
