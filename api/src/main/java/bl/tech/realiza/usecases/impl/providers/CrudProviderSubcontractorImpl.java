package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSubcontractor;
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
public class CrudProviderSubcontractorImpl implements CrudProviderSubcontractor {

    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final EmailSender emailSender;
    private final FileRepository fileRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;

    @Override
    public ProviderResponseDto save(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        if (providerSubcontractorRequestDto.getSupplier() == null || providerSubcontractorRequestDto.getSupplier().isEmpty()) {
            throw new BadRequestException("Invalid supplier");
        }

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(providerSubcontractorRequestDto.getSupplier());
        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Provider supplier not found"));

        List<DocumentProviderSupplier> documentSupplier = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupName(providerSubcontractorRequestDto.getSupplier(),"Documento empresa");
        List<DocumentMatrix> documentMatrixList = documentSupplier.stream()
                .map(DocumentProviderSupplier::getDocumentMatrix)
                .toList();

        ProviderSubcontractor newProviderSubcontractor = ProviderSubcontractor.builder()
                .cnpj(providerSubcontractorRequestDto.getCnpj())
                .corporateName(providerSubcontractorRequestDto.getCorporateName())
                .tradeName(providerSubcontractorRequestDto.getTradeName())
                .email(providerSubcontractorRequestDto.getEmail())
                .cep(providerSubcontractorRequestDto.getCep())
                .state(providerSubcontractorRequestDto.getState())
                .city(providerSubcontractorRequestDto.getCity())
                .address(providerSubcontractorRequestDto.getAddress())
                .number(providerSubcontractorRequestDto.getNumber())
                .providerSupplier(providerSupplier)
                .build();

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(newProviderSubcontractor);

        List<DocumentProviderSubcontractor> documentProviderSubcontractors = documentMatrixList.stream()
                .map(docMatrix -> DocumentProviderSubcontractor.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .providerSubcontractor(savedProviderSubcontractor)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentProviderSubcontractorRepository.saveAll(documentProviderSubcontractors);

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSubcontractor.getIdProvider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .tradeName(savedProviderSubcontractor.getTradeName())
                .corporateName(savedProviderSubcontractor.getCorporateName())
                .email(savedProviderSubcontractor.getEmail())
                .cep(savedProviderSubcontractor.getCep())
                .state(savedProviderSubcontractor.getState())
                .city(savedProviderSubcontractor.getCity())
                .address(savedProviderSubcontractor.getAddress())
                .number(savedProviderSubcontractor.getNumber())
                .supplier(savedProviderSubcontractor.getProviderSupplier().getIdProvider())
                .build();

        return providerSubcontractorResponse;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        FileDocument fileDocument = null;

        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(id);
        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new NotFoundException("Provider subcontractor not found"));

        if (providerSubcontractor.getLogo() != null) {
            Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(providerSubcontractor.getLogo()));
            fileDocument = fileDocumentOptional.orElseThrow(() -> new NotFoundException("Logo not found"));
        }

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .idProvider(providerSubcontractor.getIdProvider())
                .cnpj(providerSubcontractor.getCnpj())
                .tradeName(providerSubcontractor.getTradeName())
                .corporateName(providerSubcontractor.getCorporateName())
                .logoData(fileDocument != null ? fileDocument.getData() : null)
                .email(providerSubcontractor.getEmail())
                .cep(providerSubcontractor.getCep())
                .state(providerSubcontractor.getState())
                .city(providerSubcontractor.getCity())
                .address(providerSubcontractor.getAddress())
                .number(providerSubcontractor.getNumber())
                .supplier(providerSubcontractor.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(providerSubcontractorResponse);
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        Page<ProviderSubcontractor> providerSubcontractorPage = providerSubcontractorRepository.findAllByIsActiveIsTrue(pageable);

        Page<ProviderResponseDto> providerSubcontractorResponseDtoPage = providerSubcontractorPage.map(
                providerSubcontractor -> {
                    FileDocument fileDocument = null;
                    if (providerSubcontractor.getLogo() != null && !providerSubcontractor.getLogo().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(providerSubcontractor.getLogo()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return ProviderResponseDto.builder()
                            .idProvider(providerSubcontractor.getIdProvider())
                            .cnpj(providerSubcontractor.getCnpj())
                            .tradeName(providerSubcontractor.getTradeName())
                            .corporateName(providerSubcontractor.getCorporateName())
                            .logoData(fileDocument != null ? fileDocument.getData() : null)
                            .email(providerSubcontractor.getEmail())
                            .cep(providerSubcontractor.getCep())
                            .state(providerSubcontractor.getState())
                            .city(providerSubcontractor.getCity())
                            .address(providerSubcontractor.getAddress())
                            .number(providerSubcontractor.getNumber())
                            .supplier(providerSubcontractor.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return providerSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<ProviderResponseDto> update(String id, ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(id);

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new NotFoundException("Provider subcontractor not found"));

        providerSubcontractor.setCnpj(providerSubcontractorRequestDto.getCnpj() != null ? providerSubcontractorRequestDto.getCnpj() : providerSubcontractor.getCnpj());
        providerSubcontractor.setTradeName(providerSubcontractorRequestDto.getTradeName() != null ? providerSubcontractorRequestDto.getTradeName() : providerSubcontractor.getTradeName());
        providerSubcontractor.setCorporateName(providerSubcontractorRequestDto.getCorporateName() != null ? providerSubcontractorRequestDto.getCorporateName() : providerSubcontractor.getCorporateName());
        providerSubcontractor.setEmail(providerSubcontractorRequestDto.getEmail() != null ? providerSubcontractorRequestDto.getEmail() : providerSubcontractor.getEmail());
        providerSubcontractor.setCep(providerSubcontractorRequestDto.getCep() != null ? providerSubcontractorRequestDto.getCep() : providerSubcontractor.getCep());
        providerSubcontractor.setState(providerSubcontractorRequestDto.getState() != null ? providerSubcontractorRequestDto.getState() : providerSubcontractor.getState());
        providerSubcontractor.setCity(providerSubcontractorRequestDto.getCity() != null ? providerSubcontractorRequestDto.getCity() : providerSubcontractor.getCity());
        providerSubcontractor.setAddress(providerSubcontractorRequestDto.getAddress() != null ? providerSubcontractorRequestDto.getAddress() : providerSubcontractor.getAddress());
        providerSubcontractor.setNumber(providerSubcontractorRequestDto.getNumber() != null ? providerSubcontractorRequestDto.getNumber() : providerSubcontractor.getNumber());

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(providerSubcontractor);

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSubcontractor.getIdProvider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .tradeName(savedProviderSubcontractor.getTradeName())
                .corporateName(savedProviderSubcontractor.getCorporateName())
                .email(savedProviderSubcontractor.getEmail())
                .cep(savedProviderSubcontractor.getCep())
                .state(savedProviderSubcontractor.getState())
                .city(savedProviderSubcontractor.getCity())
                .address(savedProviderSubcontractor.getAddress())
                .number(savedProviderSubcontractor.getNumber())
                .supplier(savedProviderSubcontractor.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(providerSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        providerSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ProviderResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ProviderSubcontractor> providerSubcontractorPage = providerSubcontractorRepository.findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        Page<ProviderResponseDto> providerSubcontractorResponseDtoPage = providerSubcontractorPage.map(
                providerSubcontractor -> {
                    FileDocument fileDocument = null;
                    if (providerSubcontractor.getLogo() != null && !providerSubcontractor.getLogo().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(providerSubcontractor.getLogo()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return ProviderResponseDto.builder()
                            .idProvider(providerSubcontractor.getIdProvider())
                            .cnpj(providerSubcontractor.getCnpj())
                            .tradeName(providerSubcontractor.getTradeName())
                            .corporateName(providerSubcontractor.getCorporateName())
                            .logoData(fileDocument != null ? fileDocument.getData() : null)
                            .email(providerSubcontractor.getEmail())
                            .cep(providerSubcontractor.getCep())
                            .state(providerSubcontractor.getState())
                            .city(providerSubcontractor.getCity())
                            .address(providerSubcontractor.getAddress())
                            .number(providerSubcontractor.getNumber())
                            .supplier(providerSubcontractor.getProviderSupplier().getIdProvider())
                            .build();
                }
        );

        return providerSubcontractorResponseDtoPage;
    }

    @Override
    public String changeLogo(String id, MultipartFile file) throws IOException {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(id);
        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new NotFoundException("Subcontractor not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            if (providerSubcontractor.getLogo() != null) {
                fileRepository.deleteById(new ObjectId(providerSubcontractor.getLogo()));
            }
            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            providerSubcontractor.setLogo(savedFileDocument.getIdDocumentAsString());
        }

        providerSubcontractorRepository.save(providerSubcontractor);

        return "Logo updated successfully";
    }
}
