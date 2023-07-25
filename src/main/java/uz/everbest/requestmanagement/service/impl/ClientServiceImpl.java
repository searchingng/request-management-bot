package uz.everbest.requestmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.everbest.requestmanagement.domain.entity.Client;
import uz.everbest.requestmanagement.repository.ClientRepository;
import uz.everbest.requestmanagement.service.ClientService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public List<Client> findAllActiveClients() {
        return clientRepository.findByIsActiveIsTrue();
    }

    @Override
    public Client findById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> findByUserId(Long userId) {
        return clientRepository.findByUserIdAndIsActiveIsTrue(userId);
    }

    @Override
    public Integer countByCompanyId(Long companyId) {
        return clientRepository.countByCompanyId(companyId);
    }
}
