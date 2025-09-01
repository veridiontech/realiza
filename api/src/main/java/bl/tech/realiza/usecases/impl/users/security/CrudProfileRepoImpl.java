package bl.tech.realiza.usecases.impl.users.security;

import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.user.security.Permission;
import bl.tech.realiza.domains.user.security.Profile;
import bl.tech.realiza.domains.user.security.ProfileRepo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.security.PermissionRepository;
import bl.tech.realiza.gateways.repositories.users.security.ProfileRepoRepository;
import bl.tech.realiza.gateways.requests.users.security.ProfileRepoRequestDto;
import bl.tech.realiza.gateways.requests.users.security.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileRepoResponseDto;
import bl.tech.realiza.usecases.interfaces.users.security.CrudProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CrudProfileRepoImpl implements CrudProfileRepo {
    private final ProfileRepoRepository profileRepoRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public ProfileRepoResponseDto save(ProfileRepoRequestDto profileRepoRequestDto) {
        Set<Permission> permissions = new HashSet<>();
        if (profileRepoRequestDto.getAdmin() == null || !profileRepoRequestDto.getAdmin()) {
            if (profileRepoRequestDto.getDashboard() != null) {
                ProfileRequestDto.DashboardRequest dashboard = profileRepoRequestDto.getDashboard();
                if (dashboard.getGeneral() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.DASHBOARD,
                                    PermissionSubTypeEnum.GENERAL,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
                if (dashboard.getProvider() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.DASHBOARD,
                                    PermissionSubTypeEnum.PROVIDER,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
                if (dashboard.getDocument() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.DASHBOARD,
                                    PermissionSubTypeEnum.DOCUMENT,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
                if (dashboard.getDocumentDetail() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.DASHBOARD,
                                    PermissionSubTypeEnum.DOCUMENT_DETAIL,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
            }
            if (profileRepoRequestDto.getDocument() != null) {
                ProfileRequestDto.DocumentRequest document = profileRepoRequestDto.getDocument();
                if (document.getView() != null) {
                    ProfileRequestDto.DocumentType view = document.getView();
                    if (view.getLaboral() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.VIEW,
                                        DocumentTypeEnum.LABORAL)
                                .ifPresent(permissions::add);
                    }
                    if (view.getWorkplaceSafety() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.VIEW,
                                        DocumentTypeEnum.WORKPLACE_SAFETY)
                                .ifPresent(permissions::add);
                    }
                    if (view.getRegistrationAndCertificates() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.VIEW,
                                        DocumentTypeEnum.REGISTRATION_AND_CERTIFICATES)
                                .ifPresent(permissions::add);
                    }
                    if (view.getGeneral() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.VIEW,
                                        DocumentTypeEnum.GENERAL)
                                .ifPresent(permissions::add);
                    }
                    if (view.getHealth() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.VIEW,
                                        DocumentTypeEnum.HEALTH)
                                .ifPresent(permissions::add);
                    }
                    if (view.getEnvironment() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.VIEW,
                                        DocumentTypeEnum.ENVIRONMENT)
                                .ifPresent(permissions::add);
                    }
                }
                if (document.getUpload() != null) {
                    ProfileRequestDto.DocumentType upload = document.getUpload();
                    if (upload.getLaboral() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.UPLOAD,
                                        DocumentTypeEnum.LABORAL)
                                .ifPresent(permissions::add);
                    }
                    if (upload.getWorkplaceSafety() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.UPLOAD,
                                        DocumentTypeEnum.WORKPLACE_SAFETY)
                                .ifPresent(permissions::add);
                    }
                    if (upload.getRegistrationAndCertificates() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.UPLOAD,
                                        DocumentTypeEnum.REGISTRATION_AND_CERTIFICATES)
                                .ifPresent(permissions::add);
                    }
                    if (upload.getGeneral() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.UPLOAD,
                                        DocumentTypeEnum.GENERAL)
                                .ifPresent(permissions::add);
                    }
                    if (upload.getHealth() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.UPLOAD,
                                        DocumentTypeEnum.HEALTH)
                                .ifPresent(permissions::add);
                    }
                    if (upload.getEnvironment() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.UPLOAD,
                                        DocumentTypeEnum.ENVIRONMENT)
                                .ifPresent(permissions::add);
                    }
                }
                if (document.getExempt() != null) {
                    ProfileRequestDto.DocumentType exempt = document.getExempt();
                    if (exempt.getLaboral() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.EXEMPT,
                                        DocumentTypeEnum.LABORAL)
                                .ifPresent(permissions::add);
                    }
                    if (exempt.getWorkplaceSafety() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.EXEMPT,
                                        DocumentTypeEnum.WORKPLACE_SAFETY)
                                .ifPresent(permissions::add);
                    }
                    if (exempt.getRegistrationAndCertificates() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.EXEMPT,
                                        DocumentTypeEnum.REGISTRATION_AND_CERTIFICATES)
                                .ifPresent(permissions::add);
                    }
                    if (exempt.getGeneral() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.EXEMPT,
                                        DocumentTypeEnum.GENERAL)
                                .ifPresent(permissions::add);
                    }
                    if (exempt.getHealth() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.EXEMPT,
                                        DocumentTypeEnum.HEALTH)
                                .ifPresent(permissions::add);
                    }
                    if (exempt.getEnvironment() != null) {
                        permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                        PermissionTypeEnum.DOCUMENT,
                                        PermissionSubTypeEnum.EXEMPT,
                                        DocumentTypeEnum.ENVIRONMENT)
                                .ifPresent(permissions::add);
                    }
                }
            }
            if (profileRepoRequestDto.getContract() != null) {
                ProfileRequestDto.ContractRequest contract = profileRepoRequestDto.getContract();
                if (contract.getFinish() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.CONTRACT,
                                    PermissionSubTypeEnum.FINISH,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
                if (contract.getSuspend() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.CONTRACT,
                                    PermissionSubTypeEnum.SUSPEND,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
                if (contract.getCreate() != null) {
                    permissionRepository.findFirstByTypeAndSubtypeAndDocumentType(
                                    PermissionTypeEnum.CONTRACT,
                                    PermissionSubTypeEnum.CREATE,
                                    DocumentTypeEnum.NONE)
                            .ifPresent(permissions::add);
                }
            }
        }

        return toDto(profileRepoRepository.save(ProfileRepo.builder()
                .name(profileRepoRequestDto.getName())
                .admin(profileRepoRequestDto.getAdmin() != null
                        ? profileRepoRequestDto.getAdmin()
                        : false)
                .permissions(permissions)
                .build()));
    }

    @Override
    public ProfileRepoResponseDto findOne(String id) {
        return toDto(profileRepoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProfileRepo not found")));
    }

    @Override
    public List<ProfileRepoResponseDto> findAll() {
        return toDto(profileRepoRepository.findAll());
    }

    @Override
    public ProfileRepoResponseDto update(String id, ProfileRepoRequestDto profileRepoRequestDto) {
        ProfileRepo profile = profileRepoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProfileRepo not found"));

        profile.setName(profileRepoRequestDto.getName() != null
                ? profileRepoRequestDto.getName()
                : profile.getName());
        profile.setAdmin(profileRepoRequestDto.getAdmin() != null
                ? profileRepoRequestDto.getAdmin()
                : profile.getAdmin());

        return toDto(profileRepoRepository.save(profile));
    }

    @Override
    public void delete(String id) {
        profileRepoRepository.deleteById(id);
    }

    @Override
    public Boolean checkIfExistsByName(String name) {
        return profileRepoRepository.existsByName(name);
    }

    private ProfileRepoResponseDto toDto(ProfileRepo profile) {
        return ProfileRepoResponseDto.builder()
                .id(profile.getId())
                .name(profile.getName())
                .admin(profile.getAdmin())
                .build();
    }

    private List<ProfileRepoResponseDto> toDto(List<ProfileRepo> profiles) {
        return profiles.stream().map(this::toDto).toList();
    }
}
