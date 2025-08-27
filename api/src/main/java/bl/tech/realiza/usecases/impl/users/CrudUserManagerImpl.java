package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.*;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.domains.user.security.Profile;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.*;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailNewUserRequestDto;
import bl.tech.realiza.gateways.requests.users.UserCreateRequestDto;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.services.auth.RandomPasswordService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.users.CrudUserManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserManagerImpl implements CrudUserManager {
    
    private final UserManagerRepository userManagerRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    private final ClientRepository clientRepository;
    private final BranchRepository branchRepository;
    private final UserClientRepository userClientRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final UserProviderSubcontractorRepository userProviderSubcontractorRepository;
    private final EmailSender emailSender;
    private final RandomPasswordService randomPasswordService;
    private final ProfileRepository profileRepository;
    private final ContractRepository contractRepository;
    private final GoogleCloudService googleCloudService;

    @Override
    public UserResponseDto save(UserManagerRequestDto userManagerRequestDto, MultipartFile file) {
        if (file != null) {
            if (file.getSize() > 1024 * 1024) { // 1 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }
        if (userManagerRequestDto.getRole() == null) {
            throw new BadRequestException("Role is required");
        }
        if (Arrays.stream(UserManagerRequestDto.Role.values())
                .noneMatch(role -> role.name().equals(userManagerRequestDto.getRole().name()))) {
            throw new BadRequestException("Invalid Role");
        }
        if (userManagerRequestDto.getPassword() == null || userManagerRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;
        String signedUrl = null;

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "user-pfp");

                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .build());

                signedUrl = googleCloudService.generateSignedUrl(savedFileDocument.getUrl(), 15);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
        }

        String encryptedPassword = passwordEncryptionService.encryptPassword(userManagerRequestDto.getPassword());

        UserManager newUserManager = UserManager.builder()
                .cpf(userManagerRequestDto.getCpf())
                .description(userManagerRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userManagerRequestDto.getPosition())
                .role(userManagerRequestDto.getRole())
                .firstName(userManagerRequestDto.getFirstName())
                .profilePicture(savedFileDocument)
                .surname(userManagerRequestDto.getSurname())
                .email(userManagerRequestDto.getEmail())
                .telephone(userManagerRequestDto.getTelephone())
                .cellphone(userManagerRequestDto.getCellphone())
                .isActive(true)
                .build();

        UserManager savedUserManager = userManagerRepository.save(newUserManager);

        return UserResponseDto.builder()
                .idUser(savedUserManager.getIdUser())
                .cpf(savedUserManager.getCpf())
                .description(savedUserManager.getDescription())
                .position(savedUserManager.getPosition())
                .role(savedUserManager.getRole())
                .firstName(savedUserManager.getFirstName())
                .surname(savedUserManager.getSurname())
                .email(savedUserManager.getEmail())
                .profilePictureSignedUrl(signedUrl)
                .telephone(savedUserManager.getTelephone())
                .cellphone(savedUserManager.getCellphone())
                .build();
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        String signedUrl = null;

        UserManager userManager = userManagerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userManager.getProfilePicture() != null) {
            if (userManager.getProfilePicture().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(userManager.getProfilePicture().getUrl(), 15);
            }
        }

        return Optional.of(UserResponseDto.builder()
                .idUser(userManager.getIdUser())
                .cpf(userManager.getCpf())
                .description(userManager.getDescription())
                .position(userManager.getPosition())
                .role(userManager.getRole())
                .firstName(userManager.getFirstName())
                .surname(userManager.getSurname())
                .profilePictureSignedUrl(signedUrl)
                .email(userManager.getEmail())
                .telephone(userManager.getTelephone())
                .cellphone(userManager.getCellphone())
                .build());
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserManager> userManagerPage = userManagerRepository.findAllByIsActiveIsTrue(pageable);

        return userManagerPage.map(
                userManager -> {
                    String signedUrl = null;
                    if (userManager.getProfilePicture() != null) {
                        if (userManager.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userManager.getProfilePicture().getUrl(), 15);
                        }
                    }
                    return UserResponseDto.builder()
                            .idUser(userManager.getIdUser())
                            .cpf(userManager.getCpf())
                            .description(userManager.getDescription())
                            .position(userManager.getPosition())
                            .role(userManager.getRole())
                            .firstName(userManager.getFirstName())
                            .surname(userManager.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .email(userManager.getEmail())
                            .telephone(userManager.getTelephone())
                            .cellphone(userManager.getCellphone())
                            .build();
                }
        );
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserManagerRequestDto userManagerRequestDto) {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);

        UserManager userManager = userManagerOptional.orElseThrow(() -> new NotFoundException("User not found"));

        userManager.setCpf(userManagerRequestDto.getCpf() != null
                ? userManagerRequestDto.getCpf()
                : userManager.getCpf());
        userManager.setDescription(userManagerRequestDto.getDescription() != null
                ? userManagerRequestDto.getDescription()
                : userManager.getDescription());
        userManager.setPosition(userManagerRequestDto.getPosition() != null
                ? userManagerRequestDto.getPosition()
                : userManager.getPosition());
        userManager.setRole(userManagerRequestDto.getRole() != null
                ? userManagerRequestDto.getRole()
                : userManager.getRole());
        userManager.setFirstName(userManagerRequestDto.getFirstName() != null
                ? userManagerRequestDto.getFirstName()
                : userManager.getFirstName());
        userManager.setSurname(userManagerRequestDto.getSurname() != null
                ? userManagerRequestDto.getSurname()
                : userManager.getSurname());
        userManager.setEmail(userManagerRequestDto.getEmail() != null
                ? userManagerRequestDto.getEmail()
                : userManager.getEmail());
        userManager.setTelephone(userManagerRequestDto.getTelephone() != null
                ? userManagerRequestDto.getTelephone()
                : userManager.getTelephone());
        userManager.setCellphone(userManagerRequestDto.getCellphone() != null
                ? userManagerRequestDto.getCellphone()
                : userManager.getCellphone());

        UserManager savedUserManager = userManagerRepository.save(userManager);

        return Optional.of(UserResponseDto.builder()
                .idUser(savedUserManager.getIdUser())
                .cpf(savedUserManager.getCpf())
                .description(savedUserManager.getDescription())
                .position(savedUserManager.getPosition())
                .role(savedUserManager.getRole())
                .firstName(savedUserManager.getFirstName())
                .surname(savedUserManager.getSurname())
                .email(savedUserManager.getEmail())
                .telephone(savedUserManager.getTelephone())
                .cellphone(savedUserManager.getCellphone())
                .build());
    }

    @Override
    public void delete(String id) {
        userManagerRepository.deleteById(id);
    }

    @Override
    public String changePassword(String id, UserManagerRequestDto userManagerRequestDto) {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);

        UserManager userManager = userManagerOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userManagerRequestDto.getPassword(), userManager.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userManager.setPassword(userManagerRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userManagerRequestDto.getNewPassword()) : userManager.getPassword());

        userManagerRepository.save(userManager);

        return "Password updated successfully";
    }

    @Override
    public String changeProfilePicture(String id, MultipartFile file) throws IOException {
        if (file != null) {
            if (file.getSize() > 1024 * 1024) { // 1 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }
        UserManager userManager = userManagerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        FileDocument savedFileDocument = null;

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "user-pfp");

                if (userManager.getProfilePicture() != null) {
                    googleCloudService.deleteFile(userManager.getProfilePicture().getUrl());
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
            userManager.setProfilePicture(savedFileDocument);
        }

        userManagerRepository.save(userManager);

        return "Profile picture updated successfully";
    }

    @Override
    public String createNewUserActivated(UserCreateRequestDto userCreateRequestDto) {

        String randomPassword = randomPasswordService.generateRandomPassword();
        String encryptedPassword = passwordEncryptionService.encryptPassword(randomPassword);

        switch (userCreateRequestDto.getEnterprise()) {
            case REALIZA -> {
                if (userCreateRequestDto.getRole() == User.Role.ROLE_REALIZA_BASIC ||
                        userCreateRequestDto.getRole() == User.Role.ROLE_REALIZA_PLUS) {
                    userManagerRepository.save(UserManager.builder()
                            .cpf(userCreateRequestDto.getCpf())
                            .description(userCreateRequestDto.getDescription())
                            .position(userCreateRequestDto.getPosition())
                            .role(userCreateRequestDto.getRole())
                            .firstName(userCreateRequestDto.getFirstName())
                            .surname(userCreateRequestDto.getSurname())
                            .email(userCreateRequestDto.getEmail())
                            .password(encryptedPassword)
                            .telephone(userCreateRequestDto.getTelephone())
                            .cellphone(userCreateRequestDto.getCellphone())
                            .isActive(true)
                            .build());
                } else {
                    throw new IllegalArgumentException("Invalid role for REALIZA enterprise");
                }
            }
            case CLIENT -> {
                if (userCreateRequestDto.getRole() == User.Role.ROLE_CLIENT_RESPONSIBLE ||
                        userCreateRequestDto.getRole() == User.Role.ROLE_CLIENT_MANAGER) {

                    Branch branch = branchRepository.findById(userCreateRequestDto.getIdEnterprise())
                                    .orElse(null);

                    if (branch == null) {
                        Client client = clientRepository.findById(userCreateRequestDto.getIdEnterprise())
                                .orElseThrow(() -> new NotFoundException("Client not found"));

                        branch = branchRepository.findFirstByClient_IdClientOrderByCreationDateAsc(client.getIdClient());
                    }

                    Profile profile = null;
                    List<Branch> branchAccessList = new ArrayList<>();
                    List<Contract> contractAccessList = new ArrayList<>();

                    if (userCreateRequestDto.getProfileId() != null && !userCreateRequestDto.getProfileId().isEmpty()) {
                        profile = profileRepository.findById(userCreateRequestDto.getProfileId())
                                .orElseThrow(() -> new NotFoundException("Profile not found"));
                    }

                    if (userCreateRequestDto.getBranchAccessIds() != null && !userCreateRequestDto.getBranchAccessIds().isEmpty()) {
                        branchAccessList = branchRepository.findAllById(userCreateRequestDto.getBranchAccessIds());
                        if (branchAccessList.isEmpty()) {
                            branchAccessList.add(branch);
                        }
                    }

                    if (userCreateRequestDto.getContractAccessIds() != null && !userCreateRequestDto.getContractAccessIds().isEmpty()) {
                        contractAccessList = contractRepository.findAllById(userCreateRequestDto.getContractAccessIds());
                    }

                    userClientRepository.save(UserClient.builder()
                            .cpf(userCreateRequestDto.getCpf())
                            .description(userCreateRequestDto.getDescription())
                            .position(userCreateRequestDto.getPosition())
                            .role(userCreateRequestDto.getRole())
                            .firstName(userCreateRequestDto.getFirstName())
                            .surname(userCreateRequestDto.getSurname())
                            .email(userCreateRequestDto.getEmail())
                            .password(encryptedPassword)
                            .telephone(userCreateRequestDto.getTelephone())
                            .cellphone(userCreateRequestDto.getCellphone())
                            .branch(branch)
                            .profile(profile)
                            .branchesAccess(branchAccessList)
                            .contractsAccess(contractAccessList)
                            .isActive(true)
                            .build());
                } else {
                    throw new IllegalArgumentException("Invalid role for CLIENT enterprise");
                }
            }
            case SUPPLIER -> {
                if (userCreateRequestDto.getRole() == User.Role.ROLE_SUPPLIER_RESPONSIBLE ||
                        userCreateRequestDto.getRole() == User.Role.ROLE_SUPPLIER_MANAGER) {
                    ProviderSupplier supplier = providerSupplierRepository.findById(userCreateRequestDto.getIdEnterprise())
                            .orElseThrow(() -> new NotFoundException("Supplier not found"));

                    userProviderSupplierRepository.save(UserProviderSupplier.builder()
                            .cpf(userCreateRequestDto.getCpf())
                            .description(userCreateRequestDto.getDescription())
                            .position(userCreateRequestDto.getPosition())
                            .role(userCreateRequestDto.getRole())
                            .firstName(userCreateRequestDto.getFirstName())
                            .surname(userCreateRequestDto.getSurname())
                            .email(userCreateRequestDto.getEmail())
                            .password(encryptedPassword)
                            .telephone(userCreateRequestDto.getTelephone())
                            .cellphone(userCreateRequestDto.getCellphone())
                            .providerSupplier(supplier)
                            .isActive(true)
                            .build());
                } else {
                    throw new IllegalArgumentException("Invalid role for SUPPLIER enterprise");
                }
            }
            case SUBCONTRACTOR -> {
                if (userCreateRequestDto.getRole() == User.Role.ROLE_SUBCONTRACTOR_RESPONSIBLE ||
                        userCreateRequestDto.getRole() == User.Role.ROLE_SUBCONTRACTOR_MANAGER) {
                    ProviderSubcontractor subcontractor = providerSubcontractorRepository.findById(userCreateRequestDto.getIdEnterprise())
                            .orElseThrow(() -> new NotFoundException("Subcontractor not found"));

                    userProviderSubcontractorRepository.save(UserProviderSubcontractor.builder()
                            .cpf(userCreateRequestDto.getCpf())
                            .description(userCreateRequestDto.getDescription())
                            .position(userCreateRequestDto.getPosition())
                            .role(userCreateRequestDto.getRole())
                            .firstName(userCreateRequestDto.getFirstName())
                            .surname(userCreateRequestDto.getSurname())
                            .email(userCreateRequestDto.getEmail())
                            .password(encryptedPassword)
                            .telephone(userCreateRequestDto.getTelephone())
                            .cellphone(userCreateRequestDto.getCellphone())
                            .providerSubcontractor(subcontractor)
                            .isActive(true)
                            .build());
                } else {
                    throw new IllegalArgumentException("Invalid role for SUBCONTRACTOR enterprise");
                }
            }
            default -> throw new BadRequestException("Unexpected value: " + userCreateRequestDto.getEnterprise());
        }

        emailSender.sendNewUserEmail(EmailNewUserRequestDto.builder()
                .email(userCreateRequestDto.getEmail())
                .password(randomPassword)
                .nameUser(userCreateRequestDto.getFirstName() + " " + userCreateRequestDto.getSurname())
                .build());

        return "User " + userCreateRequestDto.getEnterprise() + " created successfully";
    }
}
