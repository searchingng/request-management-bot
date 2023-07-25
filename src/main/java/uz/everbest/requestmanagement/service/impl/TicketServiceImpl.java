package uz.everbest.requestmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.everbest.requestmanagement.domain.entity.Ticket;
import uz.everbest.requestmanagement.repository.TicketRepository;
import uz.everbest.requestmanagement.service.TicketService;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Override
    public Integer todaysTicketCount() {
        return ticketRepository.countByToday();
    }
}
