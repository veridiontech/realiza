package bl.tech.realiza.domains.user.security;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_PERMISSION")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private PermissionTypeEnum type;
    @Builder.Default
    private PermissionSubTypeEnum subtype = PermissionSubTypeEnum.NONE;
    @Builder.Default
    private DocumentTypeEnum documentType = DocumentTypeEnum.NONE;

    @ManyToMany(mappedBy = "permissions")
    private Set<Profile> profiles;

    @ManyToMany(mappedBy = "permissions")
    private Set<ProfileRepo> profilesRepo;
}
