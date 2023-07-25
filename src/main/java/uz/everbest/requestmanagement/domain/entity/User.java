package uz.everbest.requestmanagement.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.everbest.requestmanagement.domain.enums.LanguageCode;
import uz.everbest.requestmanagement.domain.enums.UserRole;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String phone;

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private LanguageCode langCode;

    private String position;

    private String password;

    private String lastAction;

    public LanguageCode language() {
        return this.langCode;
    }

}
