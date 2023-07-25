package uz.everbest.requestmanagement.service;

import uz.everbest.requestmanagement.domain.entity.Ticket;

public interface TicketService {

    Ticket save(Ticket ticket);

    Ticket findById(Long id);

    Integer todaysTicketCount();

}
