package uz.everbest.requestmanagement.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.everbest.requestmanagement.domain.enums.CompanyStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String inn;

    private String registratedDate;
    private String registratedBy;
    private String thsht;
    private String dbibt;
    private String ifut;
    private String ustavFondi;
    private String address;
    private String phone;
    private String email;
    private String leader;
    private String founders;

    private String accountNumber;
    private String bankCode;
    private String bankName;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
