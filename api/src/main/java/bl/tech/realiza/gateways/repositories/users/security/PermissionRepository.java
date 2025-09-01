package bl.tech.realiza.gateways.repositories.users.security;

import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.user.security.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findFirstByTypeAndSubtypeAndDocumentType(PermissionTypeEnum type, PermissionSubTypeEnum subtype, DocumentTypeEnum documentType);
}
