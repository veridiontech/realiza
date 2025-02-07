package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.users.CrudUserProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springdoc.core.parsers.ReturnTypeParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserProviderSupplierImpl implements CrudUserProviderSupplier {

    private final UserProviderSupplierRepository userSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;
    private final ReturnTypeParser genericReturnTypeParser;

    @Override
    public UserResponseDto save(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        if (userProviderSupplierRequestDto.getPassword() == null || userProviderSupplierRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }
        if (userProviderSupplierRequestDto.getSupplier() == null || userProviderSupplierRequestDto.getSupplier().isEmpty()) {
            throw new BadRequestException("Invalid supplier");
        }
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(userProviderSupplierRequestDto.getSupplier());
        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        String encryptedPassword = passwordEncryptionService.encryptPassword(userProviderSupplierRequestDto.getPassword());

        UserProviderSupplier newUserSupplier = UserProviderSupplier.builder()
                .cpf(userProviderSupplierRequestDto.getCpf())
                .description(userProviderSupplierRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userProviderSupplierRequestDto.getPosition())
                .role(userProviderSupplierRequestDto.getRole())
                .firstName(userProviderSupplierRequestDto.getFirstName())
                .timeZone(userProviderSupplierRequestDto.getTimeZone())
                .surname(userProviderSupplierRequestDto.getSurname())
                .email(userProviderSupplierRequestDto.getEmail())
                .profilePicture(userProviderSupplierRequestDto.getProfilePicture())
                .telephone(userProviderSupplierRequestDto.getTelephone())
                .cellphone(userProviderSupplierRequestDto.getCellphone())
                .providerSupplier(providerSupplier)
                .build();

        UserProviderSupplier savedUserSupplier = userSupplierRepository.save(newUserSupplier);

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(savedUserSupplier.getCpf())
                .description(savedUserSupplier.getDescription())
                .position(savedUserSupplier.getPosition())
                .role(savedUserSupplier.getRole())
                .firstName(savedUserSupplier.getFirstName())
                .timeZone(savedUserSupplier.getTimeZone())
                .surname(savedUserSupplier.getSurname())
                .email(savedUserSupplier.getEmail())
                .profilePicture(savedUserSupplier.getProfilePicture())
                .telephone(savedUserSupplier.getTelephone())
                .cellphone(savedUserSupplier.getCellphone())
                .supplier(savedUserSupplier.getProviderSupplier().getIdProvider())
                .build();

        return userSupplierResponse;
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        FileDocument fileDocument = null;

        Optional<UserProviderSupplier> userProviderOptional = userSupplierRepository.findById(id);
        UserProviderSupplier userProvider = userProviderOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (userProvider.getProfilePicture() != null) {
            Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userProvider.getProfilePicture()));
            fileDocument = fileDocumentOptional.orElseThrow(() -> new NotFoundException("Profile Picture not found"));
        }

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(userProvider.getCpf())
                .description(userProvider.getDescription())
                .position(userProvider.getPosition())
                .role(userProvider.getRole())
                .firstName(userProvider.getFirstName())
                .timeZone(userProvider.getTimeZone())
                .surname(userProvider.getSurname())
                .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                .email(userProvider.getEmail())
                .profilePicture(userProvider.getProfilePicture())
                .telephone(userProvider.getTelephone())
                .cellphone(userProvider.getCellphone())
                .supplier(userProvider.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(userSupplierResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserProviderSupplier> userProviderPage = userSupplierRepository.findAll(pageable);

        Page<UserResponseDto> userSupplierResponseDtoPage = userProviderPage.map(
                userProvider -> {
                    FileDocument fileDocument = null;
                    if (userProvider.getProfilePicture() != null && !userProvider.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userProvider.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }
                    return UserResponseDto.builder()
                            .cpf(userProvider.getCpf())
                            .description(userProvider.getDescription())
                            .position(userProvider.getPosition())
                            .role(userProvider.getRole())
                            .firstName(userProvider.getFirstName())
                            .timeZone(userProvider.getTimeZone())
                            .surname(userProvider.getSurname())
                            .email(userProvider.getEmail())
                            .profilePicture(userProvider.getProfilePicture())
                            .telephone(userProvider.getTelephone())
                            .cellphone(userProvider.getCellphone())
                            .supplier(userProvider.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return userSupplierResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        Optional<UserProviderSupplier> userProviderOptional = userSupplierRepository.findById(id);

        UserProviderSupplier userProvider = userProviderOptional.orElseThrow(() -> new NotFoundException("User not found"));

        userProvider.setCpf(userProviderSupplierRequestDto.getCpf() != null ? userProviderSupplierRequestDto.getCpf() : userProvider.getCpf());
        userProvider.setDescription(userProviderSupplierRequestDto.getDescription() != null ? userProviderSupplierRequestDto.getDescription() : userProvider.getDescription());
        userProvider.setPosition(userProviderSupplierRequestDto.getPosition() != null ? userProviderSupplierRequestDto.getPosition() : userProvider.getPosition());
        userProvider.setRole(userProviderSupplierRequestDto.getRole() != null ? userProviderSupplierRequestDto.getRole() : userProvider.getRole());
        userProvider.setFirstName(userProviderSupplierRequestDto.getFirstName() != null ? userProviderSupplierRequestDto.getFirstName() : userProvider.getFirstName());
        userProvider.setTimeZone(userProviderSupplierRequestDto.getTimeZone() != null ? userProviderSupplierRequestDto.getTimeZone() : userProvider.getTimeZone());
        userProvider.setSurname(userProviderSupplierRequestDto.getSurname() != null ? userProviderSupplierRequestDto.getSurname() : userProvider.getSurname());
        userProvider.setEmail(userProviderSupplierRequestDto.getEmail() != null ? userProviderSupplierRequestDto.getEmail() : userProvider.getEmail());
        userProvider.setProfilePicture(userProviderSupplierRequestDto.getProfilePicture() != null ? userProviderSupplierRequestDto.getProfilePicture() : userProvider.getProfilePicture());
        userProvider.setTelephone(userProviderSupplierRequestDto.getTelephone() != null ? userProviderSupplierRequestDto.getTelephone() : userProvider.getTelephone());
        userProvider.setCellphone(userProviderSupplierRequestDto.getCellphone() != null ? userProviderSupplierRequestDto.getCellphone() : userProvider.getCellphone());

        UserProviderSupplier savedUserSupplier = userSupplierRepository.save(userProvider);

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(savedUserSupplier.getCpf())
                .description(savedUserSupplier.getDescription())
                .position(savedUserSupplier.getPosition())
                .role(savedUserSupplier.getRole())
                .firstName(savedUserSupplier.getFirstName())
                .timeZone(savedUserSupplier.getTimeZone())
                .surname(savedUserSupplier.getSurname())
                .email(savedUserSupplier.getEmail())
                .profilePicture(savedUserSupplier.getProfilePicture())
                .telephone(savedUserSupplier.getTelephone())
                .cellphone(savedUserSupplier.getCellphone())
                .supplier(savedUserSupplier.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(userSupplierResponse);
    }

    @Override
    public void delete(String id) {
        userSupplierRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<UserProviderSupplier> userProviderPage = userSupplierRepository.findAllByProviderSupplier_IdProviderAndRole(idSearch, User.Role.ROLE_SUPPLIER_MANAGER, pageable);

        Page<UserResponseDto> userSupplierResponseDtoPage = userProviderPage.map(
                userProvider -> {
                    FileDocument fileDocument = null;
                    if (userProvider.getProfilePicture() != null && !userProvider.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(userProvider.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }
                    return UserResponseDto.builder()
                            .cpf(userProvider.getCpf())
                            .description(userProvider.getDescription())
                            .position(userProvider.getPosition())
                            .role(userProvider.getRole())
                            .firstName(userProvider.getFirstName())
                            .timeZone(userProvider.getTimeZone())
                            .surname(userProvider.getSurname())
                            .email(userProvider.getEmail())
                            .profilePicture(userProvider.getProfilePicture())
                            .telephone(userProvider.getTelephone())
                            .cellphone(userProvider.getCellphone())
                            .supplier(userProvider.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return userSupplierResponseDtoPage;
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
        Optional<UserProviderSupplier> userProviderSupplierOptional = userSupplierRepository.findById(id);
        UserProviderSupplier userProviderSupplier = userProviderSupplierOptional.orElseThrow(() -> new NotFoundException("User not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            if (userProviderSupplier.getProfilePicture() != null) {
                fileRepository.deleteById(new ObjectId(userProviderSupplier.getProfilePicture()));
            }

            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            userProviderSupplier.setProfilePicture(savedFileDocument.getIdDocumentAsString());
        }

        userSupplierRepository.save(userProviderSupplier);

        return "Profile picture updated successfully";
    }
}
