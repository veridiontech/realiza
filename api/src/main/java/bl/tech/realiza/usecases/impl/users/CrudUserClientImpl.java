package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.users.CrudUserClient;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserClientImpl implements CrudUserClient {

    private final UserClientRepository userClientRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final CrudItemManagement crudItemManagementImpl;

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

        Optional<Branch> branchOptional = branchRepository.findById(userClientRequestDto.getBranch());
        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        String encryptedPassword = passwordEncryptionService.encryptPassword(userClientRequestDto.getPassword());

        UserClient newUserClient = UserClient.builder()
                .cpf(userClientRequestDto.getCpf())
                .description(userClientRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userClientRequestDto.getPosition())
                .role(userClientRequestDto.getRole())
                .firstName(userClientRequestDto.getFirstName())
                .surname(userClientRequestDto.getSurname())
                .email(userClientRequestDto.getEmail())
                .profilePicture(userClientRequestDto.getProfilePicture())
                .telephone(userClientRequestDto.getTelephone())
                .cellphone(userClientRequestDto.getCellphone())
                .branch(branch)
                .build();

        UserClient userClient = userClientRepository.findById(userClientRequestDto.getIdUser())
                .orElseThrow(() -> new NotFoundException("User requester not found"));

        UserClient savedUserClient = userClientRepository.save(newUserClient);

        crudItemManagementImpl.saveUserSolicitation(ItemManagementUserRequestDto.builder()
                .title(String.format("Novo usuário %s %s", savedUserClient.getFirstName() != null ? savedUserClient.getFirstName() : "", savedUserClient.getSurname() != null ? savedUserClient.getSurname() : ""))
                .details(String.format("Solicitação de adição do usuário %s %s da empresa %s a plataforma",
                        savedUserClient.getFirstName() != null ? savedUserClient.getFirstName() : "",
                        savedUserClient.getSurname() != null ? savedUserClient.getSurname() : "", savedUserClient.getBranch().getName() != null ? savedUserClient.getBranch().getName() : ""))
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
                .profilePicture(savedUserClient.getProfilePicture())
                .telephone(savedUserClient.getTelephone())
                .cellphone(savedUserClient.getCellphone())
                .branch(savedUserClient.getBranch().getIdBranch())
                .build();
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        FileDocument fileDocument = null;

        Optional<UserClient> userClientOptional = userClientRepository.findById(id);
        UserClient userClient = userClientOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (userClient.getProfilePicture() != null) {
            Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userClient.getProfilePicture()));
            fileDocument = fileDocumentOptional.orElseThrow(() -> new NotFoundException("Profile Picture not found"));
        }

        UserResponseDto userClientResponse = UserResponseDto.builder()
                .idUser(userClient.getIdUser())
                .cpf(userClient.getCpf())
                .description(userClient.getDescription())
                .position(userClient.getPosition())
                .role(userClient.getRole())
                .firstName(userClient.getFirstName())
                .surname(userClient.getSurname())
                .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                .email(userClient.getEmail())
                .profilePicture(userClient.getProfilePicture())
                .telephone(userClient.getTelephone())
                .cellphone(userClient.getCellphone())
                .branch(userClient.getBranch().getIdBranch())
                .build();

        return Optional.of(userClientResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAllByIsActiveIsTrue(pageable);

        Page<UserResponseDto> userClientResponseDtoPage = userClientPage.map(
                userClient -> {
                    FileDocument fileDocument = null;
                    if (userClient.getProfilePicture() != null && !userClient.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userClient.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return UserResponseDto.builder()
                            .idUser(userClient.getIdUser())
                            .cpf(userClient.getCpf())
                            .description(userClient.getDescription())
                            .position(userClient.getPosition())
                            .role(userClient.getRole())
                            .firstName(userClient.getFirstName())
                            .surname(userClient.getSurname())
                            .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                            .email(userClient.getEmail())
                            .profilePicture(userClient.getProfilePicture())
                            .telephone(userClient.getTelephone())
                            .cellphone(userClient.getCellphone())
                            .branch(userClient.getBranch().getIdBranch())
                            .build();
                }
        );

        return userClientResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserClientRequestDto userClientRequestDto) {
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new NotFoundException("User not found"));

        userClient.setCpf(userClientRequestDto.getCpf() != null ? userClientRequestDto.getCpf() : userClient.getCpf());
        userClient.setDescription(userClientRequestDto.getDescription() != null ? userClientRequestDto.getDescription() : userClient.getDescription());
        userClient.setPosition(userClientRequestDto.getPosition() != null ? userClientRequestDto.getPosition() : userClient.getPosition());
        userClient.setRole(userClientRequestDto.getRole() != null ? userClientRequestDto.getRole() : userClient.getRole());
        userClient.setFirstName(userClientRequestDto.getFirstName() != null ? userClientRequestDto.getFirstName() : userClient.getFirstName());
        userClient.setSurname(userClientRequestDto.getSurname() != null ? userClientRequestDto.getSurname() : userClient.getSurname());
        userClient.setEmail(userClientRequestDto.getEmail() != null ? userClientRequestDto.getEmail() : userClient.getEmail());
        userClient.setProfilePicture(userClientRequestDto.getProfilePicture() != null ? userClientRequestDto.getProfilePicture() : userClient.getProfilePicture());
        userClient.setTelephone(userClientRequestDto.getTelephone() != null ? userClientRequestDto.getTelephone() : userClient.getTelephone());
        userClient.setCellphone(userClientRequestDto.getCellphone() != null ? userClientRequestDto.getCellphone() : userClient.getCellphone());

        UserClient savedUserClient = userClientRepository.save(userClient);

        UserResponseDto userClientResponse = UserResponseDto.builder()
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .description(savedUserClient.getDescription())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .firstName(savedUserClient.getFirstName())
                .surname(savedUserClient.getSurname())
                .email(savedUserClient.getEmail())
                .profilePicture(savedUserClient.getProfilePicture())
                .telephone(savedUserClient.getTelephone())
                .cellphone(savedUserClient.getCellphone())
                .branch(savedUserClient.getBranch().getIdBranch())
                .build();

        return Optional.of(userClientResponse);
    }

    @Override
    public void delete(String id) {
        userClientRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAllByBranch_IdBranchAndRoleAndIsActiveIsTrue(idSearch, User.Role.ROLE_CLIENT_MANAGER, pageable);

        Page<UserResponseDto> userClientResponseDtoPage = userClientPage.map(
                userClient -> {
                    FileDocument fileDocument = null;
                    if (userClient.getProfilePicture() != null && !userClient.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userClient.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return UserResponseDto.builder()
                            .idUser(userClient.getIdUser())
                            .cpf(userClient.getCpf())
                            .description(userClient.getDescription())
                            .position(userClient.getPosition())
                            .role(userClient.getRole())
                            .firstName(userClient.getFirstName())
                            .surname(userClient.getSurname())
                            .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                            .email(userClient.getEmail())
                            .profilePicture(userClient.getProfilePicture())
                            .telephone(userClient.getTelephone())
                            .cellphone(userClient.getCellphone())
                            .branch(userClient.getBranch().getIdBranch())
                            .build();
                }
        );

        return userClientResponseDtoPage;
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
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);
        UserClient userClient = userClientOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();


            if (userClient.getProfilePicture() != null) {
                fileRepository.deleteById(new ObjectId(userClient.getProfilePicture()));
            }
            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            userClient.setProfilePicture(savedFileDocument.getIdDocumentAsString());
        }

        userClientRepository.save(userClient);

        return "Profile picture updated successfully";
    }
}
