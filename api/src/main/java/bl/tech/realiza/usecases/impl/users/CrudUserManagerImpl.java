package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserManagerRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.users.CrudUserManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserManagerImpl implements CrudUserManager {
    
    private final UserManagerRepository userManagerRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    
    @Override
    public UserResponseDto save(UserManagerRequestDto userManagerRequestDto) {
        String encryptedPassword = passwordEncryptionService.encryptPassword(userManagerRequestDto.getPassword());

        UserManager newUserManager = UserManager.builder()
                .cpf(userManagerRequestDto.getCpf())
                .description(userManagerRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userManagerRequestDto.getPosition())
                .role(userManagerRequestDto.getRole())
                .firstName(userManagerRequestDto.getFirstName())
                .timeZone(userManagerRequestDto.getTimeZone())
                .surname(userManagerRequestDto.getSurname())
                .email(userManagerRequestDto.getEmail())
                .profilePicture(userManagerRequestDto.getProfilePicture())
                .telephone(userManagerRequestDto.getTelephone())
                .cellphone(userManagerRequestDto.getCellphone())
                .build();

        UserManager savedUserManager = userManagerRepository.save(newUserManager);

        UserResponseDto userManagerResponse = UserResponseDto.builder()
                .idUser(savedUserManager.getIdUser())
                .cpf(savedUserManager.getCpf())
                .description(savedUserManager.getDescription())
                .position(savedUserManager.getPosition())
                .role(savedUserManager.getRole())
                .firstName(savedUserManager.getFirstName())
                .timeZone(savedUserManager.getTimeZone())
                .surname(savedUserManager.getSurname())
                .email(savedUserManager.getEmail())
                .profilePicture(savedUserManager.getProfilePicture())
                .telephone(savedUserManager.getTelephone())
                .cellphone(savedUserManager.getCellphone())
                .build();

        return userManagerResponse;
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        FileDocument fileDocument = null;

        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);
        UserManager userManager = userManagerOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userManager.getProfilePicture() != null) {
            Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userManager.getProfilePicture()));
            fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("Profile Picture not found"));
        }

        UserResponseDto userManagerResponse = UserResponseDto.builder()
                .idUser(userManager.getIdUser())
                .cpf(userManager.getCpf())
                .description(userManager.getDescription())
                .position(userManager.getPosition())
                .role(userManager.getRole())
                .firstName(userManager.getFirstName())
                .timeZone(userManager.getTimeZone())
                .surname(userManager.getSurname())
                .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                .email(userManager.getEmail())
                .profilePicture(userManager.getProfilePicture())
                .telephone(userManager.getTelephone())
                .cellphone(userManager.getCellphone())
                .build();

        return Optional.of(userManagerResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserManager> userManagerPage = userManagerRepository.findAll(pageable);

        Page<UserResponseDto> userManagerResponseDtoPage = userManagerPage.map(
                userManager -> {
                    FileDocument fileDocument = null;
                    if (userManager.getProfilePicture() != null && !userManager.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userManager.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }
                    return UserResponseDto.builder()
                            .idUser(userManager.getIdUser())
                            .cpf(userManager.getCpf())
                            .description(userManager.getDescription())
                            .position(userManager.getPosition())
                            .role(userManager.getRole())
                            .firstName(userManager.getFirstName())
                            .timeZone(userManager.getTimeZone())
                            .surname(userManager.getSurname())
                            .email(userManager.getEmail())
                            .profilePicture(userManager.getProfilePicture())
                            .telephone(userManager.getTelephone())
                            .cellphone(userManager.getCellphone())
                            .build();
                }
        );
        return userManagerResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserManagerRequestDto userManagerRequestDto) {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);

        UserManager userManager = userManagerOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        userManager.setCpf(userManagerRequestDto.getCpf() != null ? userManagerRequestDto.getCpf() : userManager.getCpf());
        userManager.setDescription(userManagerRequestDto.getDescription() != null ? userManagerRequestDto.getDescription() : userManager.getDescription());
        userManager.setPosition(userManagerRequestDto.getPosition() != null ? userManagerRequestDto.getPosition() : userManager.getPosition());
        userManager.setRole(userManagerRequestDto.getRole() != null ? userManagerRequestDto.getRole() : userManager.getRole());
        userManager.setFirstName(userManagerRequestDto.getFirstName() != null ? userManagerRequestDto.getFirstName() : userManager.getFirstName());
        userManager.setTimeZone(userManagerRequestDto.getTimeZone() != null ? userManagerRequestDto.getTimeZone() : userManager.getTimeZone());
        userManager.setSurname(userManagerRequestDto.getSurname() != null ? userManagerRequestDto.getSurname() : userManager.getSurname());
        userManager.setEmail(userManagerRequestDto.getEmail() != null ? userManagerRequestDto.getEmail() : userManager.getEmail());
        userManager.setProfilePicture(userManagerRequestDto.getProfilePicture() != null ? userManagerRequestDto.getProfilePicture() : userManager.getProfilePicture());
        userManager.setTelephone(userManagerRequestDto.getTelephone() != null ? userManagerRequestDto.getTelephone() : userManager.getTelephone());
        userManager.setCellphone(userManagerRequestDto.getCellphone() != null ? userManagerRequestDto.getCellphone() : userManager.getCellphone());
        userManager.setIsActive(userManagerRequestDto.getIsActive() != null ? userManagerRequestDto.getIsActive() : userManager.getIsActive());

        UserManager savedUserManager = userManagerRepository.save(userManager);

        UserResponseDto userManagerResponse = UserResponseDto.builder()
                .idUser(savedUserManager.getIdUser())
                .cpf(savedUserManager.getCpf())
                .description(savedUserManager.getDescription())
                .position(savedUserManager.getPosition())
                .role(savedUserManager.getRole())
                .firstName(savedUserManager.getFirstName())
                .timeZone(savedUserManager.getTimeZone())
                .surname(savedUserManager.getSurname())
                .email(savedUserManager.getEmail())
                .profilePicture(savedUserManager.getProfilePicture())
                .telephone(savedUserManager.getTelephone())
                .cellphone(savedUserManager.getCellphone())
                .build();

        return Optional.of(userManagerResponse);
    }

    @Override
    public void delete(String id) {
        userManagerRepository.deleteById(id);
    }

    @Override
    public String changePassword(String id, UserManagerRequestDto userManagerRequestDto) {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);

        UserManager userManager = userManagerOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userManagerRequestDto.getPassword(), userManager.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userManager.setPassword(userManagerRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userManagerRequestDto.getPassword()) : userManager.getPassword());

        userManagerRepository.save(userManager);

        return "Password updated successfully";
    }

    @Override
    public String changeProfilePicture(String id, MultipartFile file) throws IOException {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);
        UserManager userManager = userManagerOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            if (userManager.getProfilePicture() != null) {
                fileRepository.deleteById(new ObjectId(userManager.getProfilePicture()));
            }
            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            userManager.setProfilePicture(savedFileDocument.getIdDocumentAsString());
        }

        userManagerRepository.save(userManager);

        return "Profile picture updated successfully";
    }
}
