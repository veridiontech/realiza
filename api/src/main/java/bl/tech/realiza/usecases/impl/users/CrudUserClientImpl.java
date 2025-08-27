package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.security.Profile;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.users.CrudUserClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static bl.tech.realiza.domains.user.User.Role.*;

@Service
@RequiredArgsConstructor
public class CrudUserClientImpl implements CrudUserClient {

    private final UserClientRepository userClientRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final CrudItemManagement crudItemManagement;
    private final ProfileRepository profileRepository;
    private final ContractRepository contractRepository;
    private final GoogleCloudService googleCloudService;

    @Override
    public UserResponseDto save(UserClientRequestDto userClientRequestDto) {
        if (Arrays.stream(UserClientRequestDto.Role.values())
                .noneMatch(role -> role.name().equals(userClientRequestDto.getRole().name()))) {
            throw new BadRequestException("Invalid Role");
        }
        if (userClientRequestDto.getPassword() == null || userClientRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }
        if (userClientRequestDto.getBranch() == null || userClientRequestDto.getBranch().isEmpty()) {
            throw new BadRequestException("Invalid branch");
        }

        Branch branch = branchRepository.findById(userClientRequestDto.getBranch())
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Profile profile = null;
        List<Branch> branchAccessList = new ArrayList<>();
        List<Contract> contractAccessList = new ArrayList<>();

        if (userClientRequestDto.getProfileId() != null && !userClientRequestDto.getProfileId().isEmpty()) {
            profile = profileRepository.findById(userClientRequestDto.getProfileId())
                    .orElseThrow(() -> new NotFoundException("Profile not found"));
        }

        if (userClientRequestDto.getBranchAccessIds() != null && !userClientRequestDto.getBranchAccessIds().isEmpty()) {
            branchAccessList = branchRepository.findAllById(userClientRequestDto.getBranchAccessIds());
            if (branchAccessList.isEmpty()) {
                branchAccessList.add(branch);
            }
        }

        if (userClientRequestDto.getContractAccessIds() != null && !userClientRequestDto.getContractAccessIds().isEmpty()) {
            contractAccessList = contractRepository.findAllById(userClientRequestDto.getContractAccessIds());
        }

        UserClient newUserClient = UserClient.builder()
                .cpf(userClientRequestDto.getCpf())
                .description(userClientRequestDto.getDescription())
                .password(passwordEncryptionService.encryptPassword(userClientRequestDto.getPassword()))
                .position(userClientRequestDto.getPosition())
                .role(userClientRequestDto.getRole())
                .firstName(userClientRequestDto.getFirstName())
                .surname(userClientRequestDto.getSurname())
                .email(userClientRequestDto.getEmail())
                .telephone(userClientRequestDto.getTelephone())
                .cellphone(userClientRequestDto.getCellphone())
                .branch(branch)
                .profile(profile)
                .branchesAccess(branchAccessList)
                .contractsAccess(contractAccessList)
                .build();

        UserClient userClient = userClientRepository.findById(userClientRequestDto.getIdUser())
                .orElseThrow(() -> new NotFoundException("User requester not found"));

        UserClient savedUserClient = userClientRepository.save(newUserClient);

        crudItemManagement.saveUserSolicitation(ItemManagementUserRequestDto.builder()
                .solicitationType(ItemManagement.SolicitationType.CREATION)
                .idRequester(savedUserClient.getIdUser())
                .idNewUser(userClient.getIdUser())
                .build());

        return UserResponseDto.builder()
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .description(savedUserClient.getDescription())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .firstName(savedUserClient.getFirstName())
                .surname(savedUserClient.getSurname())
                .email(savedUserClient.getEmail())
                .telephone(savedUserClient.getTelephone())
                .cellphone(savedUserClient.getCellphone())
                .branch(savedUserClient.getBranch() != null
                        ? savedUserClient.getBranch().getIdBranch()
                        : null)
                .build();
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {

        UserClient userClient = userClientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String signedUrl = null;
        if (userClient.getProfilePicture() != null) {
            if (userClient.getProfilePicture().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(userClient.getProfilePicture().getUrl(), 15);
            }
        }

        return Optional.of(UserResponseDto.builder()
                .idUser(userClient.getIdUser())
                .cpf(userClient.getCpf())
                .description(userClient.getDescription())
                .position(userClient.getPosition())
                .role(userClient.getRole())
                .firstName(userClient.getFirstName())
                .surname(userClient.getSurname())
                .profilePictureSignedUrl(signedUrl)
                .email(userClient.getEmail())
                .telephone(userClient.getTelephone())
                .cellphone(userClient.getCellphone())
                .branch(userClient.getBranch().getIdBranch())
                .build());
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAllByIsActiveIsTrue(pageable);

        return userClientPage.map(
                userClient -> {
                    String signedUrl = null;
                    if (userClient.getProfilePicture() != null) {
                        if (userClient.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userClient.getProfilePicture().getUrl(), 15);
                        }
                    }

                    return UserResponseDto.builder()
                            .idUser(userClient.getIdUser())
                            .cpf(userClient.getCpf())
                            .description(userClient.getDescription())
                            .position(userClient.getPosition())
                            .role(userClient.getRole())
                            .firstName(userClient.getFirstName())
                            .surname(userClient.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .email(userClient.getEmail())
                            .telephone(userClient.getTelephone())
                            .cellphone(userClient.getCellphone())
                            .branch(userClient.getBranch().getIdBranch())
                            .build();
                }
        );
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserClientRequestDto userClientRequestDto) {
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new NotFoundException("User not found"));

        userClient.setCpf(userClientRequestDto.getCpf() != null
                ? userClientRequestDto.getCpf()
                : userClient.getCpf());
        userClient.setDescription(userClientRequestDto.getDescription() != null
                ? userClientRequestDto.getDescription()
                : userClient.getDescription());
        userClient.setPosition(userClientRequestDto.getPosition() != null
                ? userClientRequestDto.getPosition()
                : userClient.getPosition());
        userClient.setRole(userClientRequestDto.getRole() != null
                ? userClientRequestDto.getRole()
                : userClient.getRole());
        userClient.setFirstName(userClientRequestDto.getFirstName() != null
                ? userClientRequestDto.getFirstName()
                : userClient.getFirstName());
        userClient.setSurname(userClientRequestDto.getSurname() != null
                ? userClientRequestDto.getSurname()
                : userClient.getSurname());
        userClient.setEmail(userClientRequestDto.getEmail() != null
                ? userClientRequestDto.getEmail()
                : userClient.getEmail());
        userClient.setTelephone(userClientRequestDto.getTelephone() != null
                ? userClientRequestDto.getTelephone()
                : userClient.getTelephone());
        userClient.setCellphone(userClientRequestDto.getCellphone() != null
                ? userClientRequestDto.getCellphone()
                : userClient.getCellphone());

        UserClient savedUserClient = userClientRepository.save(userClient);

        return Optional.of(UserResponseDto.builder()
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .description(savedUserClient.getDescription())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .firstName(savedUserClient.getFirstName())
                .surname(savedUserClient.getSurname())
                .email(savedUserClient.getEmail())
                .telephone(savedUserClient.getTelephone())
                .cellphone(savedUserClient.getCellphone())
                .branch(savedUserClient.getBranch().getIdBranch())
                .build());
    }

    @Override
    public void delete(String id) {
        userClientRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAllByBranch_IdBranchAndRoleAndIsActiveIsTrue(idSearch, ROLE_CLIENT_MANAGER, pageable);

        Page<UserResponseDto> userClientResponseDtoPage = userClientPage.map(
                userClient -> {
                    String signedUrl = null;
                    if (userClient.getProfilePicture() != null) {
                        if (userClient.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userClient.getProfilePicture().getUrl(), 15);
                        }
                    }

                    return UserResponseDto.builder()
                            .idUser(userClient.getIdUser())
                            .cpf(userClient.getCpf())
                            .description(userClient.getDescription())
                            .position(userClient.getPosition())
                            .role(userClient.getRole())
                            .firstName(userClient.getFirstName())
                            .surname(userClient.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .email(userClient.getEmail())
                            .telephone(userClient.getTelephone())
                            .cellphone(userClient.getCellphone())
                            .branch(userClient.getBranch().getIdBranch())
                            .build();
                }
        );

        return userClientResponseDtoPage;
    }

    @Override
    public Page<UserResponseDto> findAllInnactiveAndActiveByClient(String idSearch, Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAllByBranch_IdBranchAndRole(idSearch, ROLE_CLIENT_MANAGER, pageable);

        return userClientPage.map(
                userClient -> {
                    String signedUrl = null;
                    if (userClient.getProfilePicture() != null) {
                        if (userClient.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userClient.getProfilePicture().getUrl(), 15);
                        }
                    }

                    return UserResponseDto.builder()
                            .idUser(userClient.getIdUser())
                            .cpf(userClient.getCpf())
                            .description(userClient.getDescription())
                            .position(userClient.getPosition())
                            .role(userClient.getRole())
                            .firstName(userClient.getFirstName())
                            .surname(userClient.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .email(userClient.getEmail())
                            .telephone(userClient.getTelephone())
                            .cellphone(userClient.getCellphone())
                            .branch(userClient.getBranch().getIdBranch())
                            .build();
                }
        );
    }

    @Override
    public String changePassword(String id, UserClientRequestDto userClientRequestDto) {
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userClientRequestDto.getPassword(), userClient.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userClient.setPassword(userClientRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userClientRequestDto.getPassword()) : userClient.getPassword());

        userClientRepository.save(userClient);

        return "Password updated successfully";
    }

    @Override
    public String changeProfilePicture(String id, MultipartFile file) throws IOException {
        if (file != null) {
            if (file.getSize() > 1024 * 1024) { // 1 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }
        FileDocument savedFileDocument = null;
        UserClient userClient = userClientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "user-pfp");

                if (userClient.getProfilePicture() != null) {
                    googleCloudService.deleteFile(userClient.getProfilePicture().getUrl());
                }
                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .build());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            userClient.setProfilePicture(savedFileDocument);
        }

        userClientRepository.save(userClient);

        return "Profile picture updated successfully";
    }
}
