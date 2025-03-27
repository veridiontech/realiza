package bl.tech.realiza.domains.user;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APP_USER",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "cpf"),
        @UniqueConstraint(columnNames = "email")
})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "company")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idUser;
    private String cpf;
    private String description;
    private String password;
    private String position;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String firstName;
    private String surname;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToMany(mappedBy = "requester")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ItemManagement> userRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "responsible")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Contract> contracts;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Notification> notifications;

    public enum Role {
        ROLE_ADMIN,
        ROLE_REALIZA_PLUS,
        ROLE_REALIZA_BASIC,
        ROLE_CLIENT_RESPONSIBLE,
        ROLE_CLIENT_MANAGER,
        ROLE_SUPPLIER_RESPONSIBLE,
        ROLE_SUPPLIER_MANAGER,
        ROLE_SUBCONTRACTOR_RESPONSIBLE,
        ROLE_SUBCONTRACTOR_MANAGER,
        ROLE_VIEWER
    }
}
