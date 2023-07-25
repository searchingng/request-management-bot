package uz.everbest.requestmanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.everbest.requestmanagement.domain.entity.Ticket;

public interface TicketRepository extends CrudRepository<Ticket, Long> {

    @Query(value = "SELECT count(*) FROM ticket t WHERE date(t.created_at) = current_date", nativeQuery = true)
    Integer countByToday();
}
