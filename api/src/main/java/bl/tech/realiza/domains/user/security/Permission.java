package bl.tech.realiza.domains.user.security;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "USER_PERMISSION",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_permission_type_subtype_doc",
                columnNames = {"type", "subtype", "document_type"}
        ),
        indexes = {
                @Index(name = "idx_permission_type_subtype", columnList = "type,subtype")
        }
)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private PermissionTypeEnum type;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PermissionSubTypeEnum subtype = PermissionSubTypeEnum.NONE;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DocumentTypeEnum documentType = DocumentTypeEnum.NONE;

    @ManyToMany(mappedBy = "permissions")
    private Set<Profile> profiles;

    @ManyToMany(mappedBy = "permissions")
    private Set<ProfileRepo> profilesRepo;
}
