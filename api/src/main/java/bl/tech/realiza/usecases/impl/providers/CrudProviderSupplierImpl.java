package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudProviderSupplierImpl implements CrudProviderSupplier {

    private final ProviderSupplierRepository providerSupplierRepository;
    private final BranchRepository branchRepository;
    private final FileRepository fileRepository;

    @Override
    public ProviderResponseDto save(ProviderSupplierRequestDto providerSupplierRequestDto) {
        if (providerSupplierRequestDto.getBranches() == null || providerSupplierRequestDto.getBranches().isEmpty()) {
            throw new BadRequestException("Invalid branches");
        }
        List<Branch> branches = branchRepository.findAllById(providerSupplierRequestDto.getBranches());
        if (branches.isEmpty()) {
            throw new NotFoundException("Branches not found");
        }

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(providerSupplierRequestDto.getCnpj())
                .tradeName(providerSupplierRequestDto.getTradeName())
                .corporateName(providerSupplierRequestDto.getCorporateName())
                .email(providerSupplierRequestDto.getEmail())
                .cep(providerSupplierRequestDto.getCep())
                .state(providerSupplierRequestDto.getState())
                .city(providerSupplierRequestDto.getCity())
                .address(providerSupplierRequestDto.getAddress())
                .number(providerSupplierRequestDto.getNumber())
                .branches(branches)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .tradeName(savedProviderSupplier.getTradeName())
                .corporateName(savedProviderSupplier.getCorporateName())
                .email(savedProviderSupplier.getEmail())
                .cep(savedProviderSupplier.getCep())
                .state(savedProviderSupplier.getState())
                .city(savedProviderSupplier.getCity())
                .address(savedProviderSupplier.getAddress())
                .number(savedProviderSupplier.getNumber())
                .branches(savedProviderSupplier.getBranches().stream().map(
                        branch -> ProviderResponseDto.BranchDto.builder()
                                .idBranch(branch.getIdBranch())
                                .nameBranch(branch.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return providerSupplierResponse;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(id);

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Provider not found"));

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .idProvider(providerSupplier.getIdProvider())
                .cnpj(providerSupplier.getCnpj())
                .tradeName(providerSupplier.getTradeName())
                .corporateName(providerSupplier.getCorporateName())
                .email(providerSupplier.getEmail())
                .cep(providerSupplier.getCep())
                .state(providerSupplier.getState())
                .city(providerSupplier.getCity())
                .address(providerSupplier.getAddress())
                .number(providerSupplier.getNumber())
                .branches(providerSupplier.getBranches().stream().map(
                                branch -> ProviderResponseDto.BranchDto.builder()
                                        .idBranch(branch.getIdBranch())
                                        .nameBranch(branch.getName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        Page<ProviderSupplier> providerSupplierPage = providerSupplierRepository.findAll(pageable);

        Page<ProviderResponseDto> providerSupplierResponseDtoPage = providerSupplierPage.map(
                providerSupplier -> {
                    FileDocument fileDocument = null;
                    if (providerSupplier.getLogo() != null && !providerSupplier.getLogo().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(providerSupplier.getLogo()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return ProviderResponseDto.builder()
                            .idProvider(providerSupplier.getIdProvider())
                            .cnpj(providerSupplier.getCnpj())
                            .tradeName(providerSupplier.getTradeName())
                            .corporateName(providerSupplier.getCorporateName())
                            .logoData(fileDocument != null ? fileDocument.getData() : null)
                            .email(providerSupplier.getEmail())
                            .cep(providerSupplier.getCep())
                            .state(providerSupplier.getState())
                            .city(providerSupplier.getCity())
                            .address(providerSupplier.getAddress())
                            .number(providerSupplier.getNumber())
                            .branches(providerSupplier.getBranches().stream().map(
                                            branch -> ProviderResponseDto.BranchDto.builder()
                                                    .idBranch(branch.getIdBranch())
                                                    .nameBranch(branch.getName())
                                                    .build())
                                    .collect(Collectors.toList()))
                            .build();
                }
        );
        return providerSupplierResponseDtoPage;
    }

    @Override
    public Optional<ProviderResponseDto> update(String id, ProviderSupplierRequestDto providerSupplierRequestDto) {

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(id);
        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Provider not found"));

        List<Branch> branches = branchRepository.findAllById(providerSupplierRequestDto.getBranches());
        if (branches.isEmpty()) {
            throw new NotFoundException("Branches not found");
        }

        providerSupplier.setCnpj(providerSupplierRequestDto.getCnpj() != null ? providerSupplierRequestDto.getCnpj() : providerSupplier.getCnpj());
        providerSupplier.setTradeName(providerSupplierRequestDto.getTradeName() != null ? providerSupplierRequestDto.getTradeName() : providerSupplier.getTradeName());
        providerSupplier.setCorporateName(providerSupplierRequestDto.getCorporateName() != null ? providerSupplierRequestDto.getCorporateName() : providerSupplier.getCorporateName());
        providerSupplier.setEmail(providerSupplierRequestDto.getEmail() != null ? providerSupplierRequestDto.getEmail() : providerSupplier.getEmail());
        providerSupplier.setCep(providerSupplierRequestDto.getCep() != null ? providerSupplierRequestDto.getCep() : providerSupplier.getCep());
        providerSupplier.setState(providerSupplierRequestDto.getState() != null ? providerSupplierRequestDto.getState() : providerSupplier.getState());
        providerSupplier.setCity(providerSupplierRequestDto.getCity() != null ? providerSupplierRequestDto.getCity() : providerSupplier.getCity());
        providerSupplier.setAddress(providerSupplierRequestDto.getAddress() != null ? providerSupplierRequestDto.getAddress() : providerSupplier.getAddress());
        providerSupplier.setNumber(providerSupplierRequestDto.getNumber() != null ? providerSupplierRequestDto.getNumber() : providerSupplier.getNumber());
        providerSupplier.setBranches(providerSupplierRequestDto.getBranches() != null ? branches : providerSupplier.getBranches());

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(providerSupplier);

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .tradeName(savedProviderSupplier.getTradeName())
                .corporateName(savedProviderSupplier.getCorporateName())
                .email(savedProviderSupplier.getEmail())
                .cep(savedProviderSupplier.getCep())
                .state(savedProviderSupplier.getState())
                .city(savedProviderSupplier.getCity())
                .address(savedProviderSupplier.getAddress())
                .number(savedProviderSupplier.getNumber())
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public void delete(String id) {
        providerSupplierRepository.deleteById(id);
    }

    @Override
    public Page<ProviderResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<ProviderSupplier> providerSupplierPage = providerSupplierRepository.findAllByBranches_IdBranch(idSearch, pageable);

        Page<ProviderResponseDto> providerSupplierResponseDtoPage = providerSupplierPage.map(
                providerSupplier -> {
                    FileDocument fileDocument = null;
                    if (providerSupplier.getLogo() != null && !providerSupplier.getLogo().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(providerSupplier.getLogo()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return ProviderResponseDto.builder()
                            .idProvider(providerSupplier.getIdProvider())
                            .cnpj(providerSupplier.getCnpj())
                            .tradeName(providerSupplier.getTradeName())
                            .corporateName(providerSupplier.getCorporateName())
                            .logoData(fileDocument != null ? fileDocument.getData() : null)
                            .email(providerSupplier.getEmail())
                            .cep(providerSupplier.getCep())
                            .state(providerSupplier.getState())
                            .city(providerSupplier.getCity())
                            .address(providerSupplier.getAddress())
                            .number(providerSupplier.getNumber())
                            .branches(providerSupplier.getBranches().stream().map(
                                            branch -> ProviderResponseDto.BranchDto.builder()
                                                    .idBranch(branch.getIdBranch())
                                                    .nameBranch(branch.getName())
                                                    .build())
                                    .collect(Collectors.toList()))
                            .build();
                }
        );
        return providerSupplierResponseDtoPage;
    }

    @Override
    public String changeLogo(String id, MultipartFile file) throws IOException {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(id);
        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            if (providerSupplier.getLogo() != null) {
                fileRepository.deleteById(new ObjectId(providerSupplier.getLogo()));
            }
            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            providerSupplier.setLogo(savedFileDocument.getIdDocumentAsString());
        }

        providerSupplierRepository.save(providerSupplier);

        return "Logo updated successfully";
    }

    @Override
    public String addBranch(String providerId, List<String> idBranch) {
        if (idBranch == null || idBranch.isEmpty()) {
            throw new BadRequestException("Invalid branch");
        }
        if (providerId == null || providerId.isEmpty()) {
            throw new BadRequestException("Invalid provider");
        }

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));

        List<Branch> branches = branchRepository.findAllById(idBranch);
        if (branches.isEmpty()) {
            throw new NotFoundException("Branches not found");
        }

        providerSupplier.getBranches().addAll(branches);

        providerSupplierRepository.save(providerSupplier);

        return "Branches added successfully to provider";
    }
}
