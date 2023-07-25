package uz.everbest.requestmanagement.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.everbest.requestmanagement.domain.enums.TicketStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer messageId;

    private String body;

    @ManyToOne
    @JoinColumn(name = "from_id", insertable=false, updatable=false)
    private Client from;

    @Column(name = "from_id")
    private Long fromId;

    private String filesIds;

    @ManyToOne
    @JoinColumn(name = "accepted_by_id", insertable=false, updatable=false)
    private User acceptedBy;

    @Column(name = "accepted_by_id")
    private Long acceptedById;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

}
