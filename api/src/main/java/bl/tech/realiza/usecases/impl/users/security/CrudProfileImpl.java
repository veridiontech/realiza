package bl.tech.realiza.usecases.impl.users.security;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.user.security.Permission;
import bl.tech.realiza.domains.user.security.Profile;
import bl.tech.realiza.domains.user.security.ProfileRepo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.users.security.PermissionRepository;
import bl.tech.realiza.gateways.repositories.users.security.ProfileRepoRepository;
import bl.tech.realiza.gateways.repositories.users.security.ProfileRepository;
import bl.tech.realiza.gateways.requests.users.security.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.PermissionResponse;
import bl.tech.realiza.gateways.responses.users.profile.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileResponse;
import bl.tech.realiza.gateways.responses.users.profile.ProfileResponseDto;
import bl.tech.realiza.usecases.interfaces.users.security.CrudProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrudProfileImpl implements CrudProfile {
    private final ProfileRepository profileRepository;
    private final ClientRepository clientRepository;
    private final ProfileRepoRepository profileRepoRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public ProfileResponseDto save(ProfileRequestDto profileRequestDto) {
        Client client = clientRepository.findById(profileRequestDto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));
        Set<Permission> permissions = new HashSet<>();
        if (profileRequestDto.getAdmin() == null || !profileRequestDto.getAdmin()) {
            if (profileRequestDto.getDashboard() != null) {
                ProfileRequestDto.DashboardRequest dashboard = profileRequestDto.getDashboard();
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
            if (profileRequestDto.getDocument() != null) {
                ProfileRequestDto.DocumentRequest document = profileRequestDto.getDocument();
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
            if (profileRequestDto.getContract() != null) {
                ProfileRequestDto.ContractRequest contract = profileRequestDto.getContract();
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

        return toDto(profileRepository.save(Profile.builder()
                        .name(profileRequestDto.getName())
                        .admin(profileRequestDto.getAdmin() != null
                                ? profileRequestDto.getAdmin()
                                : false)
                        .permissions(permissions)
                        .client(client)
                .build()));
    }

    @Override
    public ProfileResponseDto findOne(String id) {
        return toDto(profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found")));
    }

    @Override
    public List<ProfileResponseDto> findAll() {
        return toDto(profileRepository.findAll());
    }

    @Override
    public List<ProfileNameResponseDto> findAllByClientId(String clientId) {
        return profileRepository.findAllByClient_IdClient(clientId).stream().map(
                profile -> ProfileNameResponseDto.builder()
                        .id(profile.getId())
                        .profileName(profile.getName())
                        .build()
        ).toList();
    }

    @Override
    public ProfileResponseDto update(String id, ProfileRequestDto profileRequestDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        profile.setName(profileRequestDto.getName() != null
                ? profileRequestDto.getName()
                : profile.getName());
        profile.setAdmin(profileRequestDto.getAdmin() != null
                ? profileRequestDto.getAdmin()
                : profile.getAdmin());

        return null;
    }

    @Override
    public void delete(String id) {
        profileRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void transferFromRepoToClient(String clientId) {
        log.info("Started setup client profiles ⌛ {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        List<ProfileRepo> profileRepos = profileRepoRepository.findAll();
        List<Profile> profiles = new ArrayList<>();
        for (ProfileRepo profileRepo : profileRepos) {
            profiles.add(
                    Profile.builder()
                            .name(profileRepo.getName())
                            .description(profileRepo.getDescription())
                            .admin(profileRepo.getAdmin())
                            .permissions(profileRepo.getPermissions())
                            .client(client)
                            .build()
            );

            if (profiles.size() == 50) {
                profileRepository.saveAll(profiles);
                profiles.clear();
            }
        }

        if (!profiles.isEmpty()) {
            profileRepository.saveAll(profiles);
        }
        log.info("Finished setup client profiles ✔️ {}", clientId);
    }

    @Override
    public ProfileResponse findOneFull(String id) {
        Profile profile = profileRepository.findById(id)
                .orElse(null);
        Set<Permission> permissions = null;
        if (profile != null) {
            permissions = profile.getPermissions();
        } else {
            ProfileRepo profileRepo = profileRepoRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Profile not found"));
            permissions = profileRepo.getPermissions();
        }
        ProfileResponse response = ProfileResponse.builder().build();
        ProfileResponse.DashboardResponse dashboard = ProfileResponse.DashboardResponse.builder().build();
        ProfileResponse.DocumentResponse document = ProfileResponse.DocumentResponse.builder().build();
        ProfileResponse.ContractResponse contract = ProfileResponse.ContractResponse.builder().build();
        response.setDashboard(dashboard);
        response.setDocument(document);
        response.setContract(contract);
        ProfileResponse.DocumentType view = ProfileResponse.DocumentType.builder().build();
        ProfileResponse.DocumentType upload = ProfileResponse.DocumentType.builder().build();
        ProfileResponse.DocumentType exempt = ProfileResponse.DocumentType.builder().build();
        response.getDocument().setView(view);
        response.getDocument().setUpload(upload);
        response.getDocument().setExempt(exempt);
        for (Permission permission : permissions) {
            switch (permission.getType()) {
                case ADM -> {
                    response.setAdmin(Boolean.TRUE);
                }
                case CONTRACT -> {
                    switch (permission.getSubtype()) {
                        case FINISH -> {
                            response.getContract().setFinish(Boolean.TRUE);
                        }
                        case SUSPEND -> {
                            response.getContract().setSuspend(Boolean.TRUE);
                        }
                        case CREATE -> {
                            response.getContract().setCreate(Boolean.TRUE);
                        }
                    }
                }
                case DASHBOARD -> {
                    switch (permission.getSubtype()) {
                        case GENERAL -> {
                            response.getDashboard().setGeneral(Boolean.TRUE);
                        }
                        case PROVIDER -> {
                            response.getDashboard().setProvider(Boolean.TRUE);
                        }
                        case DOCUMENT -> {
                            response.getDashboard().setDocument(Boolean.TRUE);
                        }
                        case DOCUMENT_DETAIL -> {
                            response.getDashboard().setDocumentDetail(Boolean.TRUE);
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + permission.getSubtype());
                    }
                }
                case DOCUMENT -> {
                    switch (permission.getSubtype()) {
                        case VIEW -> {
                            switch (permission.getDocumentType()) {
                                case LABORAL -> {
                                    response.getDocument().getView().setLaboral(Boolean.TRUE);
                                }
                                case WORKPLACE_SAFETY -> {
                                    response.getDocument().getView().setWorkplaceSafety(Boolean.TRUE);
                                }
                                case REGISTRATION_AND_CERTIFICATES -> {
                                    response.getDocument().getView().setRegistrationAndCertificates(Boolean.TRUE);
                                }
                                case GENERAL -> {
                                    response.getDocument().getView().setGeneral(Boolean.TRUE);
                                }
                                case HEALTH -> {
                                    response.getDocument().getView().setHealth(Boolean.TRUE);
                                }
                                case ENVIRONMENT -> {
                                    response.getDocument().getView().setEnvironment(Boolean.TRUE);
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + permission.getDocumentType());
                            }
                        }
                        case UPLOAD -> {
                            switch (permission.getDocumentType()) {
                                case LABORAL -> {
                                    response.getDocument().getUpload().setLaboral(Boolean.TRUE);
                                }
                                case WORKPLACE_SAFETY -> {
                                    response.getDocument().getUpload().setWorkplaceSafety(Boolean.TRUE);
                                }
                                case REGISTRATION_AND_CERTIFICATES -> {
                                    response.getDocument().getUpload().setRegistrationAndCertificates(Boolean.TRUE);
                                }
                                case GENERAL -> {
                                    response.getDocument().getUpload().setGeneral(Boolean.TRUE);
                                }
                                case HEALTH -> {
                                    response.getDocument().getUpload().setHealth(Boolean.TRUE);
                                }
                                case ENVIRONMENT -> {
                                    response.getDocument().getUpload().setEnvironment(Boolean.TRUE);
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + permission.getDocumentType());
                            }
                        }
                        case EXEMPT -> {
                            switch (permission.getDocumentType()) {
                                case LABORAL -> {
                                    response.getDocument().getExempt().setLaboral(Boolean.TRUE);
                                }
                                case WORKPLACE_SAFETY -> {
                                    response.getDocument().getExempt().setWorkplaceSafety(Boolean.TRUE);
                                }
                                case REGISTRATION_AND_CERTIFICATES -> {
                                    response.getDocument().getExempt().setRegistrationAndCertificates(Boolean.TRUE);
                                }
                                case GENERAL -> {
                                    response.getDocument().getExempt().setGeneral(Boolean.TRUE);
                                }
                                case HEALTH -> {
                                    response.getDocument().getExempt().setHealth(Boolean.TRUE);
                                }
                                case ENVIRONMENT -> {
                                    response.getDocument().getExempt().setEnvironment(Boolean.TRUE);
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + permission.getDocumentType());
                            }
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + permission.getSubtype());
                    }
                }
                case RECEPTION -> {
                    response.setReception(Boolean.TRUE);
                }
            }
        }
        return response;
    }

    private ProfileResponseDto toDto(Profile profile) {
        return ProfileResponseDto.builder()
                .id(profile.getId())
                .name(profile.getName())
                .admin(profile.getAdmin())
                .permissions(toPermissionDto(profile.getPermissions()))
                .clientId(profile.getClient() != null
                        ? profile.getClient().getIdClient()
                        : null)
                .build();
    }

    private List<ProfileResponseDto> toDto(List<Profile> profiles) {
        return profiles.stream().map(this::toDto).toList();
    }

    private PermissionResponse toPermissionDto(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .type(permission.getType())
                .subType(permission.getSubtype())
                .documentType(permission.getDocumentType())
                .build();
    }

    private Set<PermissionResponse> toPermissionDto(Set<Permission> permissions) {
        return permissions.stream().map(this::toPermissionDto).collect(Collectors.toSet());
    }
}
