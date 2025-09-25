package bl.tech.realiza.domains.user;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.security.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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
    private String telephone;
    private String cellphone;
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private String forgotPasswordCode;
    private LocalDateTime forgotPasswordCodeDate;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_ACCESS",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "contractId")
    )
    private List<Contract> contractsAccess;

    @ManyToMany
    @JoinTable(
            name = "BRANCH_ACCESS",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "branchId")
    )
    private List<Branch> branchesAccess;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "requester", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemManagement> userRequest;

    @JsonIgnore
    @JsonManagedReference
    @OneToOne(mappedBy = "newUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ItemManagement newUserSolicitation;

    @JsonIgnore
    @OneToMany(mappedBy = "responsible")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Contract> contracts;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Notification> notifications;

    @ManyToOne
    @JoinColumn(name = "profileId")
    private Profile profile;

    @OneToOne
    private FileDocument profilePicture;

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

    public String getFullName() {
        return String.format("%s %s", this.firstName != null ? this.firstName : "", this.surname != null ? this.surname : "").trim();
    }
}
