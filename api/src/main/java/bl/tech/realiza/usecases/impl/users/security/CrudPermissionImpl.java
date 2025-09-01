package bl.tech.realiza.usecases.impl.users.security;

import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.security.Permission;
import bl.tech.realiza.domains.user.security.Profile;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.security.PermissionRepository;
import bl.tech.realiza.gateways.requests.users.security.CreatePermissionRequest;
import bl.tech.realiza.gateways.responses.users.profile.PermissionResponse;
import bl.tech.realiza.usecases.interfaces.users.security.CrudPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CrudPermissionImpl implements CrudPermission {
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse save(CreatePermissionRequest request) {
        return toDto(permissionRepository.save(toEntityCreate(request)));
    }

    @Override
    public PermissionResponse findOne(String id) {
        return toDto(permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Permission not found")));
    }

    @Override
    public List<PermissionResponse> findAll() {
        return toDto(permissionRepository.findAll());
    }

    private Permission toEntityCreate(CreatePermissionRequest request) {
        return Permission.builder()
                .type(request.getType())
                .subtype(request.getSubType())
                .documentType(request.getDocumentType())
                .build();
    }

    private PermissionResponse toDto(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .type(permission.getType())
                .subType(permission.getSubtype())
                .documentType(permission.getDocumentType())
                .build();
    }

    private List<PermissionResponse> toDto(List<Permission> permissions) {
        return permissions.stream().map(this::toDto).toList();
    }

    public Boolean hasPermission(User user, PermissionTypeEnum type, PermissionSubTypeEnum subType, DocumentTypeEnum documentType) {
        if (user.getProfile() != null) {
            Profile profile = user.getProfile();
            if (profile.getAdmin()) {
                return true;
            } else {
                if (user.getProfile().getPermissions() != null) {
                    Set<Permission> permissions = user.getProfile().getPermissions();
                    switch (type) {
                        case CONTRACT -> {
                            switch (subType) {
                                case FINISH, SUSPEND, CREATE -> {
                                    return permissions.stream()
                                            .anyMatch(permission -> permission.getType().equals(type)
                                                    && permission.getSubtype().equals(subType));
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + subType);
                            }
                        }
                        case DASHBOARD -> {
                            switch (subType) {
                                case GENERAL, PROVIDER, DOCUMENT, DOCUMENT_DETAIL -> {
                                    return permissions.stream()
                                            .anyMatch(permission -> permission.getType().equals(type)
                                                    && permission.getSubtype().equals(subType));
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + subType);
                            }
                        }
                        case DOCUMENT -> {
                            switch (subType) {
                                case VIEW, UPLOAD, EXEMPT -> {
                                    switch (documentType) {
                                        case LABORAL, WORKPLACE_SAFETY, REGISTRATION_AND_CERTIFICATES, GENERAL,
                                             HEALTH, ENVIRONMENT -> {
                                            return permissions.stream()
                                                    .anyMatch(permission -> permission.getType().equals(type)
                                                            && permission.getSubtype().equals(subType)
                                                            && permission.getDocumentType().equals(documentType));
                                        }
                                        default -> throw new IllegalStateException("Unexpected value: " + documentType);
                                    }
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + subType);
                            }
                        }
                        case RECEPTION -> {
                            return permissions.stream()
                                    .anyMatch(permission -> permission.getType().equals(type)
                                            && permission.getSubtype().equals(subType));
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + type);
                    }
                }
            }
        }
        return null;
    }
}
