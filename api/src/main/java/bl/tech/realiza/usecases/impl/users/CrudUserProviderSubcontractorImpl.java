package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSubcontractorRepository;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.users.CrudUserProviderSubcontractor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserProviderSubcontractorImpl implements CrudUserProviderSubcontractor {

    private final UserProviderSubcontractorRepository userSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final EmailSender emailSender;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    private final GoogleCloudService googleCloudService;

    @Override
    public UserResponseDto save(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        if (Arrays.stream(UserProviderSubcontractorRequestDto.Role.values())
                .noneMatch(role -> role.name().equals(userProviderSubcontractorRequestDto.getRole().name()))) {
            throw new BadRequestException("Invalid Role");
        }
        if (userProviderSubcontractorRequestDto.getPassword() == null || userProviderSubcontractorRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }
        if (userProviderSubcontractorRequestDto.getSubcontractor() == null || userProviderSubcontractorRequestDto.getSubcontractor().isEmpty()) {
            throw new BadRequestException("Invalid subcontractor");
        }

        ProviderSubcontractor providerSubcontractor = providerSubcontractorRepository.findById(userProviderSubcontractorRequestDto.getSubcontractor())
                .orElseThrow(() -> new NotFoundException("Subcontractor not found"));

        String encryptedPassword = passwordEncryptionService.encryptPassword(userProviderSubcontractorRequestDto.getPassword());

        UserProviderSubcontractor newUserSubcontractor = UserProviderSubcontractor.builder()
                .cpf(userProviderSubcontractorRequestDto.getCpf())
                .description(userProviderSubcontractorRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userProviderSubcontractorRequestDto.getPosition())
                .role(userProviderSubcontractorRequestDto.getRole())
                .firstName(userProviderSubcontractorRequestDto.getFirstName())
                .surname(userProviderSubcontractorRequestDto.getSurname())
                .email(userProviderSubcontractorRequestDto.getEmail())
                .telephone(userProviderSubcontractorRequestDto.getTelephone())
                .cellphone(userProviderSubcontractorRequestDto.getCellphone())
                .providerSubcontractor(providerSubcontractor)
                .build();

        UserProviderSubcontractor savedUserSubcontractor = userSubcontractorRepository.save(newUserSubcontractor);

        return UserResponseDto.builder()
                .cpf(savedUserSubcontractor.getCpf())
                .description(savedUserSubcontractor.getDescription())
                .position(savedUserSubcontractor.getPosition())
                .role(savedUserSubcontractor.getRole())
                .firstName(savedUserSubcontractor.getFirstName())
                .surname(savedUserSubcontractor.getSurname())
                .email(savedUserSubcontractor.getEmail())
                .telephone(savedUserSubcontractor.getTelephone())
                .cellphone(savedUserSubcontractor.getCellphone())
                .subcontractor(savedUserSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        UserProviderSubcontractor userSubcontractor = userSubcontractorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String signedUrl = null;
        if (userSubcontractor.getProfilePicture() != null) {
            if (userSubcontractor.getProfilePicture().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(userSubcontractor.getProfilePicture().getUrl(), 15);
            }
        }

        return Optional.of(UserResponseDto.builder()
                .cpf(userSubcontractor.getCpf())
                .description(userSubcontractor.getDescription())
                .position(userSubcontractor.getPosition())
                .role(userSubcontractor.getRole())
                .firstName(userSubcontractor.getFirstName())
                .surname(userSubcontractor.getSurname())
                .profilePictureSignedUrl(signedUrl)
                .email(userSubcontractor.getEmail())
                .telephone(userSubcontractor.getTelephone())
                .cellphone(userSubcontractor.getCellphone())
                .subcontractor(userSubcontractor.getProviderSubcontractor().getIdProvider())
                .build());
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserProviderSubcontractor> userSubcontractorPage = userSubcontractorRepository.findAllByIsActiveIsTrue(pageable);

        return userSubcontractorPage.map(
                userSubcontractor -> {
                    String signedUrl = null;
                    if (userSubcontractor.getProfilePicture() != null) {
                        if (userSubcontractor.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userSubcontractor.getProfilePicture().getUrl(), 15);
                        }
                    }
                    return UserResponseDto.builder()
                            .cpf(userSubcontractor.getCpf())
                            .description(userSubcontractor.getDescription())
                            .position(userSubcontractor.getPosition())
                            .role(userSubcontractor.getRole())
                            .firstName(userSubcontractor.getFirstName())
                            .surname(userSubcontractor.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .email(userSubcontractor.getEmail())
                            .telephone(userSubcontractor.getTelephone())
                            .cellphone(userSubcontractor.getCellphone())
                            .subcontractor(userSubcontractor.getProviderSubcontractor().getIdProvider())
                            .build();
                }

        );
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        Optional<UserProviderSubcontractor> userSubcontractorOptional = userSubcontractorRepository.findById(id);

        UserProviderSubcontractor userSubcontractor = userSubcontractorOptional.orElseThrow(() -> new NotFoundException("User not found"));

        userSubcontractor.setCpf(userProviderSubcontractorRequestDto.getCpf() != null
                ? userProviderSubcontractorRequestDto.getCpf()
                : userSubcontractor.getCpf());
        userSubcontractor.setDescription(userProviderSubcontractorRequestDto.getDescription() != null
                ? userProviderSubcontractorRequestDto.getDescription()
                : userSubcontractor.getDescription());
        userSubcontractor.setPosition(userProviderSubcontractorRequestDto.getPosition() != null
                ? userProviderSubcontractorRequestDto.getPosition()
                : userSubcontractor.getPosition());
        userSubcontractor.setRole(userProviderSubcontractorRequestDto.getRole() != null
                ? userProviderSubcontractorRequestDto.getRole()
                : userSubcontractor.getRole());
        userSubcontractor.setFirstName(userProviderSubcontractorRequestDto.getFirstName() != null
                ? userProviderSubcontractorRequestDto.getFirstName()
                : userSubcontractor.getFirstName());
        userSubcontractor.setSurname(userProviderSubcontractorRequestDto.getSurname() != null
                ? userProviderSubcontractorRequestDto.getSurname()
                : userSubcontractor.getSurname());
        userSubcontractor.setEmail(userProviderSubcontractorRequestDto.getEmail() != null
                ? userProviderSubcontractorRequestDto.getEmail()
                : userSubcontractor.getEmail());
        userSubcontractor.setTelephone(userProviderSubcontractorRequestDto.getTelephone() != null
                ? userProviderSubcontractorRequestDto.getTelephone()
                : userSubcontractor.getTelephone());
        userSubcontractor.setCellphone(userProviderSubcontractorRequestDto.getCellphone() != null
                ? userProviderSubcontractorRequestDto.getCellphone()
                : userSubcontractor.getCellphone());

        UserProviderSubcontractor savedUserSubcontractor = userSubcontractorRepository.save(userSubcontractor);

        return Optional.of(UserResponseDto.builder()
                .cpf(savedUserSubcontractor.getCpf())
                .description(savedUserSubcontractor.getDescription())
                .position(savedUserSubcontractor.getPosition())
                .role(savedUserSubcontractor.getRole())
                .firstName(savedUserSubcontractor.getFirstName())
                .surname(savedUserSubcontractor.getSurname())
                .email(savedUserSubcontractor.getEmail())
                .telephone(savedUserSubcontractor.getTelephone())
                .cellphone(savedUserSubcontractor.getCellphone())
                .subcontractor(savedUserSubcontractor.getProviderSubcontractor().getIdProvider())
                .build());
    }

    @Override
    public void delete(String id) {
        userSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<UserProviderSubcontractor> userSubcontractorPage = userSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndRoleAndIsActiveIsTrue(idSearch, User.Role.ROLE_SUBCONTRACTOR_MANAGER, pageable);

        return userSubcontractorPage.map(
                userSubcontractor -> {
                    String signedUrl = null;
                    if (userSubcontractor.getProfilePicture() != null) {
                        if (userSubcontractor.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(userSubcontractor.getProfilePicture().getUrl(), 15);
                        }
                    }
                    return UserResponseDto.builder()
                            .cpf(userSubcontractor.getCpf())
                            .description(userSubcontractor.getDescription())
                            .position(userSubcontractor.getPosition())
                            .role(userSubcontractor.getRole())
                            .firstName(userSubcontractor.getFirstName())
                            .surname(userSubcontractor.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .email(userSubcontractor.getEmail())
                            .telephone(userSubcontractor.getTelephone())
                            .cellphone(userSubcontractor.getCellphone())
                            .subcontractor(userSubcontractor.getProviderSubcontractor().getIdProvider())
                            .build();
                }

        );
    }

    @Override
    public String changePassword(String id, UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        Optional<UserProviderSubcontractor> userProviderSubcontractorOptional = userSubcontractorRepository.findById(id);

        UserProviderSubcontractor userProviderSubcontractor = userProviderSubcontractorOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userProviderSubcontractorRequestDto.getPassword(), userProviderSubcontractor.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userProviderSubcontractor.setPassword(userProviderSubcontractorRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userProviderSubcontractorRequestDto.getPassword()) : userProviderSubcontractor.getPassword());

        userSubcontractorRepository.save(userProviderSubcontractor);

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
        UserProviderSubcontractor userProviderSubcontractor = userSubcontractorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "user-pfp");

                if (userProviderSubcontractor.getProfilePicture() != null) {
                    googleCloudService.deleteFile(userProviderSubcontractor.getProfilePicture().getUrl());
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
            userProviderSubcontractor.setProfilePicture(savedFileDocument);
        }

        userSubcontractorRepository.save(userProviderSubcontractor);

        return "Profile picture updated successfully";
    }
}
