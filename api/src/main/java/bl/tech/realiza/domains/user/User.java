package bl.tech.realiza.domains.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APP_USER")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "company")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idUser;
    @Column(unique = true)
    private String cpf;
    private String description;
    private String password;
    private String position;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String firstName;
    private TimeZone timeZone;
    private String surname;
    @Column(unique = true)
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    public enum Role {
        ROLE_ADMIN,
        ROLE_MANAGER,
        ROLE_CLIENT,
        ROLE_SUPPLIER,
        ROLE_SUBCONTRACTOR,
        ROLE_VIEWER
    }
}
