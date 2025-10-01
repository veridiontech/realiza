package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailNewUserRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.services.auth.RandomPasswordService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.users.CrudUserProviderSupplier;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.parsers.ReturnTypeParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserProviderSupplierImpl implements CrudUserProviderSupplier {

    private final UserProviderSupplierRepository userSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    private final ReturnTypeParser genericReturnTypeParser;
    private final GoogleCloudService googleCloudService;
    private final RandomPasswordService randomPasswordService;
    private final EmailSender emailSender;

    @Override
    public UserResponseDto save(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        if (Arrays.stream(UserProviderSupplierRequestDto.Role.values())
                .noneMatch(role -> role.name().equals(userProviderSupplierRequestDto.getRole().name()))) {
            throw new BadRequestException("Invalid Role");
        }
        if (userProviderSupplierRequestDto.getPassword() == null || userProviderSupplierRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }
        if (userProviderSupplierRequestDto.getSupplier() == null || userProviderSupplierRequestDto.getSupplier().isEmpty()) {
            throw new BadRequestException("Invalid supplier");
        }
        ProviderSupplier providerSupplier = providerSupplierRepository.findById(userProviderSupplierRequestDto.getSupplier())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        String randomPassword = randomPasswordService.generateRandomPassword();
        String encryptedPassword = passwordEncryptionService.encryptPassword(randomPassword);

        UserProviderSupplier newUserSupplier = UserProviderSupplier.builder()
                .cpf(userProviderSupplierRequestDto.getCpf())
                .description(userProviderSupplierRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userProviderSupplierRequestDto.getPosition())
                .role(userProviderSupplierRequestDto.getRole())
                .firstName(userProviderSupplierRequestDto.getFirstName())
                .surname(userProviderSupplierRequestDto.getSurname())
                .email(userProviderSupplierRequestDto.getEmail())
                .telephone(userProviderSupplierRequestDto.getTelephone())
                .cellphone(userProviderSupplierRequestDto.getCellphone())
                .providerSupplier(providerSupplier)
                .build();

        UserProviderSupplier savedUserSupplier = userSupplierRepository.save(newUserSupplier);

        emailSender.sendNewUserEmail(EmailNewUserRequestDto.builder()
                .email(savedUserSupplier.getEmail())
                .password(randomPassword)
                .nameUser(savedUserSupplier.getFullName())
                .build());

        return UserResponseDto.builder()
                .cpf(savedUserSupplier.getCpf())
                .description(savedUserSupplier.getDescription())
                .position(savedUserSupplier.getPosition())
                .role(savedUserSupplier.getRole())
                .firstName(savedUserSupplier.getFirstName())
                .surname(savedUserSupplier.getSurname())
                .email(savedUserSupplier.getEmail())
                .telephone(savedUserSupplier.getTelephone())
                .cellphone(savedUserSupplier.getCellphone())
                .supplier(savedUserSupplier.getProviderSupplier().getIdProvider())
                .build();
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        UserProviderSupplier userProvider = userSupplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String signedUrl = null;
        if (userProvider.getProfilePicture() != null) {
            if (userProvider.getProfilePicture().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(userProvider.getProfilePicture().getUrl(), 15);
            }
        }

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(userProvider.getCpf())
                .description(userProvider.getDescription())
                .position(userProvider.getPosition())
                .role(userProvider.getRole())
                .firstName(userProvider.getFirstName())
                .surname(userProvider.getSurname())
                .profilePictureSignedUrl(signedUrl)
                .email(userProvider.getEmail())
                .telephone(userProvider.getTelephone())
                .cellphone(userProvider.getCellphone())
                .supplier(userProvider.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(userSupplierResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserProviderSupplier> userProviderPage = userSupplierRepository.findAll(pageable);

        return userProviderPage.map(
                userProvider -> {
                    String signedUrl = null;
                    if (userProvider.getProfilePicture() != null) {
                        if (userProvider.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userProvider.getProfilePicture().getUrl(), 15);
                        }
                    }
                    return UserResponseDto.builder()
                            .cpf(userProvider.getCpf())
                            .description(userProvider.getDescription())
                            .position(userProvider.getPosition())
                            .role(userProvider.getRole())
                            .firstName(userProvider.getFirstName())
                            .surname(userProvider.getSurname())
                            .email(userProvider.getEmail())
                            .profilePictureSignedUrl(signedUrl)
                            .telephone(userProvider.getTelephone())
                            .cellphone(userProvider.getCellphone())
                            .supplier(userProvider.getProviderSupplier().getIdProvider())
                            .build();
                }
        );
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        Optional<UserProviderSupplier> userProviderOptional = userSupplierRepository.findById(id);

        UserProviderSupplier userProvider = userProviderOptional.orElseThrow(() -> new NotFoundException("User not found"));

        userProvider.setCpf(userProviderSupplierRequestDto.getCpf() != null
                ? userProviderSupplierRequestDto.getCpf()
                : userProvider.getCpf());
        userProvider.setDescription(userProviderSupplierRequestDto.getDescription() != null
                ? userProviderSupplierRequestDto.getDescription()
                : userProvider.getDescription());
        userProvider.setPosition(userProviderSupplierRequestDto.getPosition() != null
                ? userProviderSupplierRequestDto.getPosition()
                : userProvider.getPosition());
        userProvider.setRole(userProviderSupplierRequestDto.getRole() != null
                ? userProviderSupplierRequestDto.getRole()
                : userProvider.getRole());
        userProvider.setFirstName(userProviderSupplierRequestDto.getFirstName() != null
                ? userProviderSupplierRequestDto.getFirstName()
                : userProvider.getFirstName());
        userProvider.setSurname(userProviderSupplierRequestDto.getSurname() != null
                ? userProviderSupplierRequestDto.getSurname()
                : userProvider.getSurname());
        userProvider.setEmail(userProviderSupplierRequestDto.getEmail() != null
                ? userProviderSupplierRequestDto.getEmail()
                : userProvider.getEmail());
        userProvider.setTelephone(userProviderSupplierRequestDto.getTelephone() != null
                ? userProviderSupplierRequestDto.getTelephone()
                : userProvider.getTelephone());
        userProvider.setCellphone(userProviderSupplierRequestDto.getCellphone() != null
                ? userProviderSupplierRequestDto.getCellphone()
                : userProvider.getCellphone());

        UserProviderSupplier savedUserSupplier = userSupplierRepository.save(userProvider);

        return Optional.of(UserResponseDto.builder()
                .cpf(savedUserSupplier.getCpf())
                .description(savedUserSupplier.getDescription())
                .position(savedUserSupplier.getPosition())
                .role(savedUserSupplier.getRole())
                .firstName(savedUserSupplier.getFirstName())
                .surname(savedUserSupplier.getSurname())
                .email(savedUserSupplier.getEmail())
                .telephone(savedUserSupplier.getTelephone())
                .cellphone(savedUserSupplier.getCellphone())
                .supplier(savedUserSupplier.getProviderSupplier().getIdProvider())
                .build());
    }

    @Override
    public void delete(String id) {
        userSupplierRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<UserProviderSupplier> userProviderPage = userSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActiveIsTrueAndRole(idSearch, User.Role.ROLE_SUPPLIER_MANAGER, pageable);

        return userProviderPage.map(
                userProvider -> {
                    String signedUrl = null;
                    if (userProvider.getProfilePicture() != null) {
                        if (userProvider.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userProvider.getProfilePicture().getUrl(), 15);
                        }
                    }
                    return UserResponseDto.builder()
                            .cpf(userProvider.getCpf())
                            .description(userProvider.getDescription())
                            .position(userProvider.getPosition())
                            .role(userProvider.getRole())
                            .firstName(userProvider.getFirstName())
                            .surname(userProvider.getSurname())
                            .email(userProvider.getEmail())
                            .profilePictureSignedUrl(signedUrl)
                            .telephone(userProvider.getTelephone())
                            .cellphone(userProvider.getCellphone())
                            .supplier(userProvider.getProviderSupplier().getIdProvider())
                            .build();
                }
        );
    }

    @Override
    public String changePassword(String id, UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        Optional<UserProviderSupplier> userProviderSupplierOptional = userSupplierRepository.findById(id);

        UserProviderSupplier userProviderSupplier = userProviderSupplierOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userProviderSupplierRequestDto.getPassword(), userProviderSupplier.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userProviderSupplier.setPassword(userProviderSupplierRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userProviderSupplierRequestDto.getPassword()) : userProviderSupplier.getPassword());

        userSupplierRepository.save(userProviderSupplier);

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
        UserProviderSupplier userProviderSupplier = userSupplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "user-pfp");

                if (userProviderSupplier.getProfilePicture() != null) {
                    googleCloudService.deleteFile(userProviderSupplier.getProfilePicture().getUrl());
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
            userProviderSupplier.setProfilePicture(savedFileDocument);
        }

        userSupplierRepository.save(userProviderSupplier);

        return "Profile picture updated successfully";
    }
}
