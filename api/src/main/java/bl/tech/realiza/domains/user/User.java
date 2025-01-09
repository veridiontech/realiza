package bl.tech.realiza.domains.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    private String cpf;
    private String description;
    private String password;
    private String position;
    private String role;
    private String firstName;
    private TimeZone timeZone;
    private String surname;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
}
