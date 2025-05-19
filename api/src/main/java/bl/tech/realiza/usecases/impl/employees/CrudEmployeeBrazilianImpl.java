package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeBrazilian;
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
public class CrudEmployeeBrazilianImpl implements CrudEmployeeBrazilian {

    private final EmployeeBrazilianRepository employeeBrazilianRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;

    @Override
    public EmployeeResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        List<Contract> contracts = List.of();
        EmployeeBrazilian newEmployeeBrazilian = null;
        Branch branch = null;
        ProviderSupplier providerSupplier = null;
        ProviderSubcontractor providerSubcontractor = null;
        List<DocumentMatrix> documentMatrixList = List.of();

        if (employeeBrazilianRequestDto.getIdContracts() != null && !employeeBrazilianRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeBrazilianRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new NotFoundException("Contracts not found");
            }
        }

        if (employeeBrazilianRequestDto.getBranch() != null && !employeeBrazilianRequestDto.getBranch().isEmpty()) {
            Optional<Branch> branchOptional = branchRepository.findById(employeeBrazilianRequestDto.getBranch());
            branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

            List<DocumentBranch> documentBranches = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(employeeBrazilianRequestDto.getBranch(), "Documento pessoa", true);

            documentMatrixList = documentBranches.stream()
                    .map(DocumentBranch::getDocumentMatrix)
                    .toList();

        } else if (employeeBrazilianRequestDto.getSupplier() != null && !employeeBrazilianRequestDto.getSupplier().isEmpty()) {
            Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(employeeBrazilianRequestDto.getSupplier());
            providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

            List<DocumentProviderSupplier> documentProviderSuppliers = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(employeeBrazilianRequestDto.getSupplier(), "Documento pessoa", true);

            documentMatrixList = documentProviderSuppliers.stream()
                    .map(DocumentProviderSupplier::getDocumentMatrix)
                    .toList();

        } else if(employeeBrazilianRequestDto.getSubcontract() != null && !employeeBrazilianRequestDto.getSubcontract().isEmpty()) {
            Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(employeeBrazilianRequestDto.getSubcontract());

            providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new NotFoundException("Subcontractor not found"));

            List<DocumentProviderSubcontractor> documentProviderSubcontractors = documentProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(employeeBrazilianRequestDto.getSubcontract(), "Documento pessoa", true);

            documentMatrixList = documentProviderSubcontractors.stream()
                    .map(DocumentProviderSubcontractor::getDocumentMatrix)
                    .toList();
        }

        newEmployeeBrazilian = EmployeeBrazilian.builder()
                .pis(employeeBrazilianRequestDto.getPis())
                .maritalStatus(employeeBrazilianRequestDto.getMaritalStatus())
                .contractType(employeeBrazilianRequestDto.getContractType())
                .cep(employeeBrazilianRequestDto.getCep())
                .name(employeeBrazilianRequestDto.getName())
                .surname(employeeBrazilianRequestDto.getSurname())
                .address(employeeBrazilianRequestDto.getAddress())
                .country(employeeBrazilianRequestDto.getCountry())
                .acronym(employeeBrazilianRequestDto.getAcronym())
                .addressLine2(employeeBrazilianRequestDto.getAddressLine2())
                .state(employeeBrazilianRequestDto.getState())
                .birthDate(employeeBrazilianRequestDto.getBirthDate())
                .city(employeeBrazilianRequestDto.getCity())
                .postalCode(employeeBrazilianRequestDto.getPostalCode())
                .gender(employeeBrazilianRequestDto.getGender())
                .position(employeeBrazilianRequestDto.getPosition())
                .registration(employeeBrazilianRequestDto.getRegistration())
                .salary(employeeBrazilianRequestDto.getSalary())
                .cellphone(employeeBrazilianRequestDto.getCellphone())
                .platformAccess(employeeBrazilianRequestDto.getPlatformAccess())
                .telephone(employeeBrazilianRequestDto.getTelephone())
                .directory(employeeBrazilianRequestDto.getDirectory())
                .levelOfEducation(employeeBrazilianRequestDto.getLevelOfEducation())
                .cbo(employeeBrazilianRequestDto.getCboId())
                .situation(Employee.Situation.DESALOCADO)
                .admissionDate(employeeBrazilianRequestDto.getAdmissionDate())
                .cpf(employeeBrazilianRequestDto.getCpf())
                .branch(branch)
                .supplier(providerSupplier)
                .subcontract(providerSubcontractor)
                .contracts(contracts)
                .build();

        EmployeeBrazilian savedEmployeeBrazilian = employeeBrazilianRepository.save(newEmployeeBrazilian);

        List<DocumentEmployee> documentEmployeeList = documentMatrixList.stream()
                .map(docMatrix -> DocumentEmployee.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .employee(savedEmployeeBrazilian)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentEmployeeRepository.saveAll(documentEmployeeList);

        EmployeeResponseDto employeeBrazilianResponse = EmployeeResponseDto.builder()
                .idEmployee(savedEmployeeBrazilian.getIdEmployee())
                .pis(savedEmployeeBrazilian.getPis())
                .maritalStatus(savedEmployeeBrazilian.getMaritalStatus())
                .contractType(savedEmployeeBrazilian.getContractType())
                .cep(savedEmployeeBrazilian.getCep())
                .name(savedEmployeeBrazilian.getName())
                .surname(savedEmployeeBrazilian.getSurname())
                .address(savedEmployeeBrazilian.getAddress())
                .addressLine2(savedEmployeeBrazilian.getAddressLine2())
                .country(savedEmployeeBrazilian.getCountry())
                .acronym(savedEmployeeBrazilian.getAcronym())
                .state(savedEmployeeBrazilian.getState())
                .birthDate(savedEmployeeBrazilian.getBirthDate())
                .city(savedEmployeeBrazilian.getCity())
                .postalCode(savedEmployeeBrazilian.getPostalCode())
                .gender(savedEmployeeBrazilian.getGender())
                .position(savedEmployeeBrazilian.getPosition())
                .registration(savedEmployeeBrazilian.getRegistration())
                .salary(savedEmployeeBrazilian.getSalary())
                .cellphone(savedEmployeeBrazilian.getCellphone())
                .platformAccess(savedEmployeeBrazilian.getPlatformAccess())
                .telephone(savedEmployeeBrazilian.getTelephone())
                .directory(savedEmployeeBrazilian.getDirectory())
                .levelOfEducation(savedEmployeeBrazilian.getLevelOfEducation())
                .cboId(savedEmployeeBrazilian.getCbo())
                .situation(savedEmployeeBrazilian.getSituation())
                .admissionDate(savedEmployeeBrazilian.getAdmissionDate())
                .cpf(savedEmployeeBrazilian.getCpf())
                .branch(savedEmployeeBrazilian.getBranch() != null ? savedEmployeeBrazilian.getBranch().getIdBranch() : null)
                .supplier(savedEmployeeBrazilian.getSupplier() != null ? savedEmployeeBrazilian.getSupplier().getIdProvider() : null)
                .subcontract(savedEmployeeBrazilian.getSubcontract() != null ? savedEmployeeBrazilian.getSubcontract().getIdProvider() : null)
                .contracts(savedEmployeeBrazilian.getContracts().stream().map(
                        contract -> EmployeeResponseDto.ContractDto.builder()
                                .idContract(contract.getIdContract())
                                .serviceName(contract.getServiceName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return employeeBrazilianResponse;
    }

    @Override
    public Optional<EmployeeResponseDto> findOne(String id) {
        FileDocument fileDocument = null;

        Optional<EmployeeBrazilian> employeeBrazilianOptional = employeeBrazilianRepository.findById(id);
        EmployeeBrazilian employeeBrazilian = employeeBrazilianOptional.orElseThrow(() -> new NotFoundException("Employee not found"));

        if (employeeBrazilian.getProfilePicture() != null) {
            Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(employeeBrazilian.getProfilePicture()));
            fileDocument = fileDocumentOptional.orElseThrow(() -> new NotFoundException("Profile Picture not found"));
        }

        EmployeeResponseDto employeeBrazilianResponse = EmployeeResponseDto.builder()
                .idEmployee(employeeBrazilian.getIdEmployee())
                .pis(employeeBrazilian.getPis())
                .maritalStatus(employeeBrazilian.getMaritalStatus())
                .contractType(employeeBrazilian.getContractType())
                .cep(employeeBrazilian.getCep())
                .name(employeeBrazilian.getName())
                .surname(employeeBrazilian.getSurname())
                .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                .address(employeeBrazilian.getAddress())
                .addressLine2(employeeBrazilian.getAddressLine2())
                .country(employeeBrazilian.getCountry())
                .acronym(employeeBrazilian.getAcronym())
                .state(employeeBrazilian.getState())
                .birthDate(employeeBrazilian.getBirthDate())
                .city(employeeBrazilian.getCity())
                .postalCode(employeeBrazilian.getPostalCode())
                .gender(employeeBrazilian.getGender())
                .position(employeeBrazilian.getPosition())
                .registration(employeeBrazilian.getRegistration())
                .salary(employeeBrazilian.getSalary())
                .cellphone(employeeBrazilian.getCellphone())
                .platformAccess(employeeBrazilian.getPlatformAccess())
                .telephone(employeeBrazilian.getTelephone())
                .directory(employeeBrazilian.getDirectory())
                .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                .cboId(employeeBrazilian.getCbo())
                .situation(employeeBrazilian.getSituation())
                .admissionDate(employeeBrazilian.getAdmissionDate())
                .cpf(employeeBrazilian.getCpf())
                .branch(employeeBrazilian.getBranch() != null ? employeeBrazilian.getBranch().getIdBranch() : null)
                .supplier(employeeBrazilian.getSupplier() != null ? employeeBrazilian.getSupplier().getIdProvider() : null)
                .subcontract(employeeBrazilian.getSubcontract() != null ? employeeBrazilian.getSubcontract().getIdProvider() : null)
                .contracts(employeeBrazilian.getContracts().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getIdContract())
                                        .serviceName(contract.getServiceName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        return Optional.of(employeeBrazilianResponse);
    }

    @Override
    public Page<EmployeeResponseDto> findAll(Pageable pageable) {
        Page<EmployeeBrazilian> employeeBrazilianPage = employeeBrazilianRepository.findAll(pageable);

        return employeeBrazilianPage.map(
                employeeBrazilian -> {
                    FileDocument fileDocument = null;
                    if (employeeBrazilian.getProfilePicture() != null && !employeeBrazilian.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(employeeBrazilian.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return EmployeeResponseDto.builder()
                            .idEmployee(employeeBrazilian.getIdEmployee())
                            .pis(employeeBrazilian.getPis())
                            .maritalStatus(employeeBrazilian.getMaritalStatus())
                            .contractType(employeeBrazilian.getContractType())
                            .cep(employeeBrazilian.getCep())
                            .name(employeeBrazilian.getName())
                            .surname(employeeBrazilian.getSurname())
                            .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                            .address(employeeBrazilian.getAddress())
                            .country(employeeBrazilian.getCountry())
                            .acronym(employeeBrazilian.getAcronym())
                            .state(employeeBrazilian.getState())
                            .birthDate(employeeBrazilian.getBirthDate())
                            .city(employeeBrazilian.getCity())
                            .postalCode(employeeBrazilian.getPostalCode())
                            .addressLine2(employeeBrazilian.getAddressLine2())
                            .gender(employeeBrazilian.getGender())
                            .position(employeeBrazilian.getPosition())
                            .registration(employeeBrazilian.getRegistration())
                            .salary(employeeBrazilian.getSalary())
                            .cellphone(employeeBrazilian.getCellphone())
                            .platformAccess(employeeBrazilian.getPlatformAccess())
                            .telephone(employeeBrazilian.getTelephone())
                            .directory(employeeBrazilian.getDirectory())
                            .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                            .cboId(employeeBrazilian.getCbo())
                            .situation(employeeBrazilian.getSituation())
                            .admissionDate(employeeBrazilian.getAdmissionDate())
                            .cpf(employeeBrazilian.getCpf())
                            .branch(employeeBrazilian.getBranch() != null ? employeeBrazilian.getBranch().getIdBranch() : null)
                            .supplier(employeeBrazilian.getSupplier() != null ? employeeBrazilian.getSupplier().getIdProvider() : null)
                            .subcontract(employeeBrazilian.getSubcontract() != null ? employeeBrazilian.getSubcontract().getIdProvider() : null)
                            .contracts(employeeBrazilian.getContracts().stream().map(
                                            contract -> EmployeeResponseDto.ContractDto.builder()
                                                    .idContract(contract.getIdContract())
                                                    .serviceName(contract.getServiceName())
                                                    .build())
                                    .collect(Collectors.toList()))
                            .build();
                }
        );
    }

    @Override
    public Optional<EmployeeResponseDto> update(String id, EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        List<Contract> contracts = List.of();

        Optional<EmployeeBrazilian> employeeBrazilianOptional = employeeBrazilianRepository.findById(id);

        EmployeeBrazilian employeeBrazilian = employeeBrazilianOptional.orElseThrow(() -> new NotFoundException("Employee not found"));

        if (employeeBrazilianRequestDto.getIdContracts() != null && !employeeBrazilianRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeBrazilianRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new NotFoundException("Contracts not found");
            }
        }

        employeeBrazilian.setPis(employeeBrazilianRequestDto.getPis() != null ? employeeBrazilianRequestDto.getPis() : employeeBrazilian.getPis());
        employeeBrazilian.setMaritalStatus(employeeBrazilianRequestDto.getMaritalStatus() != null ? employeeBrazilianRequestDto.getMaritalStatus() : employeeBrazilian.getMaritalStatus());
        employeeBrazilian.setContractType(employeeBrazilianRequestDto.getContractType() != null ? employeeBrazilianRequestDto.getContractType() : employeeBrazilian.getContractType());
        employeeBrazilian.setCep(employeeBrazilianRequestDto.getCep() != null ? employeeBrazilianRequestDto.getCep() : employeeBrazilian.getCep());
        employeeBrazilian.setName(employeeBrazilianRequestDto.getName() != null ? employeeBrazilianRequestDto.getName() : employeeBrazilian.getName());
        employeeBrazilian.setSurname(employeeBrazilianRequestDto.getSurname() != null ? employeeBrazilianRequestDto.getSurname() : employeeBrazilian.getSurname());
        employeeBrazilian.setAddress(employeeBrazilianRequestDto.getAddress() != null ? employeeBrazilianRequestDto.getAddress() : employeeBrazilian.getAddress());
        employeeBrazilian.setCountry(employeeBrazilianRequestDto.getCountry() != null ? employeeBrazilianRequestDto.getCountry() : employeeBrazilian.getCountry());
        employeeBrazilian.setAcronym(employeeBrazilianRequestDto.getAcronym() != null ? employeeBrazilianRequestDto.getAcronym() : employeeBrazilian.getAcronym());
        employeeBrazilian.setState(employeeBrazilianRequestDto.getState() != null ? employeeBrazilianRequestDto.getState() : employeeBrazilian.getState());
        employeeBrazilian.setBirthDate(employeeBrazilianRequestDto.getBirthDate() != null ? employeeBrazilianRequestDto.getBirthDate() : employeeBrazilian.getBirthDate());
        employeeBrazilian.setCity(employeeBrazilianRequestDto.getCity() != null ? employeeBrazilianRequestDto.getCity() : employeeBrazilian.getCity());
        employeeBrazilian.setPostalCode(employeeBrazilianRequestDto.getPostalCode() != null ? employeeBrazilianRequestDto.getPostalCode() : employeeBrazilian.getPostalCode());
        employeeBrazilian.setAddressLine2(employeeBrazilianRequestDto.getAddressLine2() != null ? employeeBrazilianRequestDto.getAddressLine2() : employeeBrazilian.getAddressLine2());
        employeeBrazilian.setGender(employeeBrazilianRequestDto.getGender() != null ? employeeBrazilianRequestDto.getGender() : employeeBrazilian.getGender());
        employeeBrazilian.setPosition(employeeBrazilianRequestDto.getPosition() != null ? employeeBrazilianRequestDto.getPosition() : employeeBrazilian.getPosition());
        employeeBrazilian.setRegistration(employeeBrazilianRequestDto.getRegistration() != null ? employeeBrazilianRequestDto.getRegistration() : employeeBrazilian.getRegistration());
        employeeBrazilian.setSalary(employeeBrazilianRequestDto.getSalary() != null ? employeeBrazilianRequestDto.getSalary() : employeeBrazilian.getSalary());
        employeeBrazilian.setCellphone(employeeBrazilianRequestDto.getCellphone() != null ? employeeBrazilianRequestDto.getCellphone() : employeeBrazilian.getCellphone());
        employeeBrazilian.setPlatformAccess(employeeBrazilianRequestDto.getPlatformAccess() != null ? employeeBrazilianRequestDto.getPlatformAccess() : employeeBrazilian.getPlatformAccess());
        employeeBrazilian.setTelephone(employeeBrazilianRequestDto.getTelephone() != null ? employeeBrazilianRequestDto.getTelephone() : employeeBrazilian.getTelephone());
        employeeBrazilian.setDirectory(employeeBrazilianRequestDto.getDirectory() != null ? employeeBrazilianRequestDto.getDirectory() : employeeBrazilian.getDirectory());
        employeeBrazilian.setLevelOfEducation(employeeBrazilianRequestDto.getLevelOfEducation() != null ? employeeBrazilianRequestDto.getLevelOfEducation() : employeeBrazilian.getLevelOfEducation());
        employeeBrazilian.setCbo(employeeBrazilianRequestDto.getCboId() != null ? employeeBrazilianRequestDto.getCboId() : employeeBrazilian.getCbo());
        employeeBrazilian.setSituation(employeeBrazilianRequestDto.getSituation() != null ? employeeBrazilianRequestDto.getSituation() : employeeBrazilian.getSituation());
        employeeBrazilian.setAdmissionDate(employeeBrazilianRequestDto.getAdmissionDate() != null ? employeeBrazilianRequestDto.getAdmissionDate() : employeeBrazilian.getAdmissionDate());
        employeeBrazilian.setContracts(employeeBrazilianRequestDto.getIdContracts() != null ? contracts : employeeBrazilian.getContracts());
        employeeBrazilian.setCpf(employeeBrazilianRequestDto.getCpf() != null ? employeeBrazilianRequestDto.getCpf() : employeeBrazilian.getCpf());

        EmployeeBrazilian savedEmployeeBrazilian = employeeBrazilianRepository.save(employeeBrazilian);

        EmployeeResponseDto employeeBrazilianResponse = EmployeeResponseDto.builder()
                .idEmployee(savedEmployeeBrazilian.getIdEmployee())
                .pis(savedEmployeeBrazilian.getPis())
                .maritalStatus(savedEmployeeBrazilian.getMaritalStatus())
                .contractType(savedEmployeeBrazilian.getContractType())
                .cep(savedEmployeeBrazilian.getCep())
                .name(savedEmployeeBrazilian.getName())
                .surname(savedEmployeeBrazilian.getSurname())
                .address(savedEmployeeBrazilian.getAddress())
                .addressLine2(savedEmployeeBrazilian.getAddressLine2())
                .country(savedEmployeeBrazilian.getCountry())
                .acronym(savedEmployeeBrazilian.getAcronym())
                .state(savedEmployeeBrazilian.getState())
                .birthDate(savedEmployeeBrazilian.getBirthDate())
                .city(savedEmployeeBrazilian.getCity())
                .postalCode(savedEmployeeBrazilian.getPostalCode())
                .gender(savedEmployeeBrazilian.getGender())
                .position(savedEmployeeBrazilian.getPosition())
                .registration(savedEmployeeBrazilian.getRegistration())
                .salary(savedEmployeeBrazilian.getSalary())
                .cellphone(savedEmployeeBrazilian.getCellphone())
                .platformAccess(savedEmployeeBrazilian.getPlatformAccess())
                .telephone(savedEmployeeBrazilian.getTelephone())
                .directory(savedEmployeeBrazilian.getDirectory())
                .levelOfEducation(savedEmployeeBrazilian.getLevelOfEducation())
                .cboId(savedEmployeeBrazilian.getCbo())
                .situation(savedEmployeeBrazilian.getSituation())
                .admissionDate(savedEmployeeBrazilian.getAdmissionDate())
                .cpf(savedEmployeeBrazilian.getCpf())
                .branch(savedEmployeeBrazilian.getBranch() != null ? savedEmployeeBrazilian.getBranch().getIdBranch() : null)
                .supplier(savedEmployeeBrazilian.getSupplier() != null ? savedEmployeeBrazilian.getSupplier().getIdProvider() : null)
                .subcontract(savedEmployeeBrazilian.getSubcontract() != null ? savedEmployeeBrazilian.getSubcontract().getIdProvider() : null)
                .contracts(savedEmployeeBrazilian.getContracts().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getIdContract())
                                        .serviceName(contract.getServiceName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        return Optional.of(employeeBrazilianResponse);
    }

    @Override
    public void delete(String id) {
        employeeBrazilianRepository.deleteById(id);
    }

    @Override
    public String changeProfilePicture(String id, MultipartFile file) throws IOException {
        Optional<EmployeeBrazilian> employeeBrazilianOptional = employeeBrazilianRepository.findById(id);
        EmployeeBrazilian employeeBrazilian = employeeBrazilianOptional.orElseThrow(() -> new NotFoundException("Employee not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            if (employeeBrazilian.getProfilePicture() != null) {
                fileRepository.deleteById(new ObjectId(employeeBrazilian.getProfilePicture()));
            }
            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            employeeBrazilian.setProfilePicture(savedFileDocument.getIdDocumentAsString());
        }

        employeeBrazilianRepository.save(employeeBrazilian);

        return "Profile picture updated successfully";
    }
}
