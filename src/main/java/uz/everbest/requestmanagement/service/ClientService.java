package uz.everbest.requestmanagement.service;

import uz.everbest.requestmanagement.domain.entity.Client;

import java.util.List;

public interface ClientService {

    Client save(Client client);

    List<Client> findAllActiveClients();

    Client findById(Long id);

    List<Client> findByUserId(Long userId);

    Integer countByCompanyId(Long companyId);

}
