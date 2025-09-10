package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractEmployee;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Cbo;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.domains.employees.Position;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractEmployeeRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.CboRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.repositories.employees.PositionRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeForeigner;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudEmployeeForeignerImpl implements CrudEmployeeForeigner {

    private final EmployeeForeignerRepository employeeForeignerRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ContractRepository contractRepository;
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final CboRepository cboRepository;
    private final PositionRepository positionRepository;
    private final GoogleCloudService googleCloudService;
    private final ContractEmployeeRepository contractEmployeeRepository;

    @Override
    public EmployeeResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        List<Contract> contracts = List.of();
        EmployeeForeigner newEmployeeForeigner = null;
        Branch branch = null;
        ProviderSupplier providerSupplier = null;
        ProviderSubcontractor providerSubcontractor = null;
        List<DocumentMatrix> documentMatrixList = List.of();
        Set<ContractEmployee> contractEmployees = new HashSet<>();

        if (employeeForeignerRequestDto.getIdContracts() != null && !employeeForeignerRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeForeignerRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new NotFoundException("Contracts not found");
            }
        }
        
        if (employeeForeignerRequestDto.getBranch() != null) {
            branch = branchRepository.findById(employeeForeignerRequestDto.getBranch())
                    .orElseThrow(() -> new NotFoundException("Branch not found"));

            List<DocumentBranch> documentBranches = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_Group_GroupNameAndIsActive(employeeForeignerRequestDto.getBranch(), "Documento pessoa", true);

            documentMatrixList = documentBranches.stream()
                    .map(DocumentBranch::getDocumentMatrix)
                    .toList();
        } else if (employeeForeignerRequestDto.getSupplier() != null) {
            providerSupplier = providerSupplierRepository.findById(employeeForeignerRequestDto.getSupplier())
                    .orElseThrow(() -> new NotFoundException("Supplier not found"));

            List<DocumentProviderSupplier> documentProviderSuppliers = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndDocumentMatrix_Group_GroupNameAndIsActive(employeeForeignerRequestDto.getSupplier(), "Documento pessoa", true);

            documentMatrixList = documentProviderSuppliers.stream()
                    .map(DocumentProviderSupplier::getDocumentMatrix)
                    .toList();

        } else if(employeeForeignerRequestDto.getSubcontract() != null) {
            providerSubcontractor = providerSubcontractorRepository.findById(employeeForeignerRequestDto.getSubcontract())
                    .orElseThrow(() -> new NotFoundException("Subcontractor not found"));

            List<DocumentProviderSubcontractor> documentProviderSubcontractors = documentProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_Group_GroupNameAndIsActive(employeeForeignerRequestDto.getSubcontract(), "Documento pessoa", true);

            documentMatrixList = documentProviderSubcontractors.stream()
                    .map(DocumentProviderSubcontractor::getDocumentMatrix)
                    .toList();
        }

        Cbo cbo = cboRepository.findById(employeeForeignerRequestDto.getCboId())
                .orElseThrow(() -> new NotFoundException("CBO not found"));

        Position position = positionRepository.findById(employeeForeignerRequestDto.getPositionId())
                .orElseThrow(() -> new NotFoundException("Position not found"));

        newEmployeeForeigner = EmployeeForeigner.builder()
                .pis(employeeForeignerRequestDto.getPis())
                .maritalStatus(employeeForeignerRequestDto.getMaritalStatus())
                .contractType(employeeForeignerRequestDto.getContractType())
                .cep(employeeForeignerRequestDto.getCep())
                .name(employeeForeignerRequestDto.getName())
                .surname(employeeForeignerRequestDto.getSurname())
                .address(employeeForeignerRequestDto.getAddress())
                .country(employeeForeignerRequestDto.getCountry())
                .addressLine2(employeeForeignerRequestDto.getAddressLine2())
                .acronym(employeeForeignerRequestDto.getAcronym())
                .state(employeeForeignerRequestDto.getState())
                .birthDate(employeeForeignerRequestDto.getBirthDate())
                .city(employeeForeignerRequestDto.getCity())
                .postalCode(employeeForeignerRequestDto.getPostalCode())
                .gender(employeeForeignerRequestDto.getGender())
                .registration(employeeForeignerRequestDto.getRegistration())
                .salary(employeeForeignerRequestDto.getSalary())
                .cellphone(employeeForeignerRequestDto.getCellphone())
                .platformAccess(employeeForeignerRequestDto.getPlatformAccess())
                .telephone(employeeForeignerRequestDto.getTelephone())
                .directory(employeeForeignerRequestDto.getDirectory())
                .levelOfEducation(employeeForeignerRequestDto.getLevelOfEducation())
                .cbo(cbo)
                .position(position)
                .situation(Employee.Situation.DESALOCADO)
                .rneRnmFederalPoliceProtocol(employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(employeeForeignerRequestDto.getBrazilEntryDate())
                .passport(employeeForeignerRequestDto.getPassport())
//                .contracts(contracts)
                .branch(branch)
                .supplier(providerSupplier)
                .subcontract(providerSubcontractor)
                .build();

        if (!contracts.isEmpty()) {
            for (Contract contract : contracts) {
                ContractEmployee contractEmployee = contractEmployeeRepository.findByContract_IdContractAndEmployee_IdEmployee(contract.getIdContract(), newEmployeeForeigner.getIdEmployee())
                        .orElse(null);
                if (contractEmployee == null) {
                    contractEmployees.add(ContractEmployee.builder()
                            .contract(contract)
                            .employee(newEmployeeForeigner)
                            .build());
                } else {
                    contractEmployees.add(contractEmployee);
                }
            }
            if (newEmployeeForeigner.getContractEmployees() != null) {
                newEmployeeForeigner.getContractEmployees().addAll(contractEmployees);
            } else {
                newEmployeeForeigner.setContractEmployees(contractEmployees);
            }
        }

        EmployeeForeigner savedEmployeeForeigner = employeeForeignerRepository.save(newEmployeeForeigner);

        List<DocumentEmployee> documentEmployeeList = documentMatrixList.stream()
                .map(docMatrix -> DocumentEmployee.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .employee(savedEmployeeForeigner)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentEmployeeRepository.saveAll(documentEmployeeList);

        return EmployeeResponseDto.builder()
                .idEmployee(savedEmployeeForeigner.getIdEmployee())
                .pis(savedEmployeeForeigner.getPis())
                .maritalStatus(savedEmployeeForeigner.getMaritalStatus())
                .contractType(savedEmployeeForeigner.getContractType())
                .cep(savedEmployeeForeigner.getCep())
                .name(savedEmployeeForeigner.getName())
                .surname(savedEmployeeForeigner.getSurname())
                .address(savedEmployeeForeigner.getAddress())
                .country(savedEmployeeForeigner.getCountry())
                .addressLine2(savedEmployeeForeigner.getAddressLine2())
                .acronym(savedEmployeeForeigner.getAcronym())
                .state(savedEmployeeForeigner.getState())
                .birthDate(savedEmployeeForeigner.getBirthDate())
                .city(savedEmployeeForeigner.getCity())
                .postalCode(savedEmployeeForeigner.getPostalCode())
                .gender(savedEmployeeForeigner.getGender())
                .positionId(savedEmployeeForeigner.getPosition() != null
                        ? savedEmployeeForeigner.getPosition().getId()
                        : null)
                .position(savedEmployeeForeigner.getPosition() != null
                        ? savedEmployeeForeigner.getPosition().getTitle()
                        : null)
                .registration(savedEmployeeForeigner.getRegistration())
                .salary(savedEmployeeForeigner.getSalary())
                .cellphone(savedEmployeeForeigner.getCellphone())
                .platformAccess(savedEmployeeForeigner.getPlatformAccess())
                .telephone(savedEmployeeForeigner.getTelephone())
                .directory(savedEmployeeForeigner.getDirectory())
                .levelOfEducation(savedEmployeeForeigner.getLevelOfEducation())
                .cboId(savedEmployeeForeigner.getCbo().getId())
                .situation(savedEmployeeForeigner.getSituation())
                .rneRnmFederalPoliceProtocol(savedEmployeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(savedEmployeeForeigner.getBrazilEntryDate())
                .passport(savedEmployeeForeigner.getPassport())
                .branch(savedEmployeeForeigner.getBranch() != null ? savedEmployeeForeigner.getBranch().getIdBranch() : null)
                .supplier(savedEmployeeForeigner.getSupplier() != null ? savedEmployeeForeigner.getSupplier().getIdProvider() : null)
                .subcontract(savedEmployeeForeigner.getSubcontract() != null ? savedEmployeeForeigner.getSubcontract().getIdProvider() : null)
                .contracts(savedEmployeeForeigner.getContractEmployees().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getContract() != null
                                                ? contract.getContract().getIdContract()
                                                : null)
                                        .serviceName(contract.getContract() != null
                                                ? contract.getContract().getServiceName()
                                                : null)
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Optional<EmployeeResponseDto> findOne(String id) {
        String signedUrl = null;

        EmployeeForeigner employeeForeigner = employeeForeignerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee Foreigner not found"));

        if (employeeForeigner.getProfilePicture() != null) {
            if (employeeForeigner.getProfilePicture().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(employeeForeigner.getProfilePicture().getUrl(), 15);
            }
        }

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .idEmployee(employeeForeigner.getIdEmployee())
                .pis(employeeForeigner.getPis())
                .maritalStatus(employeeForeigner.getMaritalStatus())
                .contractType(employeeForeigner.getContractType())
                .cep(employeeForeigner.getCep())
                .name(employeeForeigner.getName())
                .surname(employeeForeigner.getSurname())
                .profilePictureSignedUrl(signedUrl)
                .address(employeeForeigner.getAddress())
                .addressLine2(employeeForeigner.getAddressLine2())
                .country(employeeForeigner.getCountry())
                .acronym(employeeForeigner.getAcronym())
                .state(employeeForeigner.getState())
                .birthDate(employeeForeigner.getBirthDate())
                .city(employeeForeigner.getCity())
                .postalCode(employeeForeigner.getPostalCode())
                .gender(employeeForeigner.getGender())
                .positionId(employeeForeigner.getPosition() != null
                        ? employeeForeigner.getPosition().getId()
                        : null)
                .position(employeeForeigner.getPosition() != null
                        ? employeeForeigner.getPosition().getTitle()
                        : null)
                .registration(employeeForeigner.getRegistration())
                .salary(employeeForeigner.getSalary())
                .cellphone(employeeForeigner.getCellphone())
                .platformAccess(employeeForeigner.getPlatformAccess())
                .telephone(employeeForeigner.getTelephone())
                .directory(employeeForeigner.getDirectory())
                .levelOfEducation(employeeForeigner.getLevelOfEducation())
                .cboId(employeeForeigner.getCbo().getId())
                .situation(employeeForeigner.getSituation())
                .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                .passport(employeeForeigner.getPassport())
                .branch(employeeForeigner.getBranch() != null
                        ? employeeForeigner.getBranch().getIdBranch()
                        : null)
                .supplier(employeeForeigner.getSupplier() != null
                        ? employeeForeigner.getSupplier().getIdProvider()
                        : null)
                .subcontract(employeeForeigner.getSubcontract() != null
                        ? employeeForeigner.getSubcontract().getIdProvider()
                        : null)
                .contracts(employeeForeigner.getContractEmployees().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getContract() != null
                                                ? contract.getContract().getIdContract()
                                                : null)
                                        .serviceName(contract.getContract() != null
                                                ? contract.getContract().getServiceName()
                                                : null)
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        return Optional.of(employeeForeignerResponse);
    }

    @Override
    public Page<EmployeeResponseDto> findAll(Pageable pageable) {
        Page<EmployeeForeigner> employeeForeignerPage = employeeForeignerRepository.findAll(pageable);

        return employeeForeignerPage.map(
                employeeForeigner -> {
                    String signedUrl = null;
                    if (employeeForeigner.getProfilePicture() != null) {
                        if (employeeForeigner.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(employeeForeigner.getProfilePicture().getUrl(), 15);
                        }
                    }
                    return EmployeeResponseDto.builder()
                            .idEmployee(employeeForeigner.getIdEmployee())
                            .pis(employeeForeigner.getPis())
                            .maritalStatus(employeeForeigner.getMaritalStatus())
                            .contractType(employeeForeigner.getContractType())
                            .cep(employeeForeigner.getCep())
                            .name(employeeForeigner.getName())
                            .surname(employeeForeigner.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .address(employeeForeigner.getAddress())
                            .addressLine2(employeeForeigner.getAddressLine2())
                            .country(employeeForeigner.getCountry())
                            .acronym(employeeForeigner.getAcronym())
                            .state(employeeForeigner.getState())
                            .birthDate(employeeForeigner.getBirthDate())
                            .city(employeeForeigner.getCity())
                            .postalCode(employeeForeigner.getPostalCode())
                            .gender(employeeForeigner.getGender())
                            .positionId(employeeForeigner.getPosition() != null
                                    ? employeeForeigner.getPosition().getId()
                                    : null)
                            .position(employeeForeigner.getPosition() != null
                                    ? employeeForeigner.getPosition().getTitle()
                                    : null)
                            .registration(employeeForeigner.getRegistration())
                            .salary(employeeForeigner.getSalary())
                            .cellphone(employeeForeigner.getCellphone())
                            .platformAccess(employeeForeigner.getPlatformAccess())
                            .telephone(employeeForeigner.getTelephone())
                            .directory(employeeForeigner.getDirectory())
                            .levelOfEducation(employeeForeigner.getLevelOfEducation())
                            .cboId(employeeForeigner.getCbo().getId())
                            .situation(employeeForeigner.getSituation())
                            .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                            .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                            .passport(employeeForeigner.getPassport())
                            .branch(employeeForeigner.getBranch() != null ? employeeForeigner.getBranch().getIdBranch() : null)
                            .supplier(employeeForeigner.getSupplier() != null ? employeeForeigner.getSupplier().getIdProvider() : null)
                            .subcontract(employeeForeigner.getSubcontract() != null ? employeeForeigner.getSubcontract().getIdProvider() : null)
                            .contracts(employeeForeigner.getContractEmployees().stream().map(
                                            contract -> EmployeeResponseDto.ContractDto.builder()
                                                    .idContract(contract.getContract() != null
                                                            ? contract.getContract().getIdContract()
                                                            : null)
                                                    .serviceName(contract.getContract() != null
                                                            ? contract.getContract().getServiceName()
                                                            : null)
                                                    .build())
                                    .collect(Collectors.toList()))
                            .build();
                }
        );
    }

    @Override
    public Optional<EmployeeResponseDto> update(String id, EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        List<Contract> contracts = List.of();
        Cbo cbo = null;
        Position position = null;
        
        Optional<EmployeeForeigner> employeeForeignerOptional = employeeForeignerRepository.findById(id);

        EmployeeForeigner employeeForeigner = employeeForeignerOptional.orElseThrow(() -> new NotFoundException("Employee Foreigner not found"));

        if (employeeForeignerRequestDto.getIdContracts() != null && !employeeForeignerRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeForeignerRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new NotFoundException("Contracts not found");
            }
        }

        if (employeeForeignerRequestDto.getCboId() != null) {
            cbo = cboRepository.findById(employeeForeignerRequestDto.getCboId())
                    .orElseThrow(() -> new NotFoundException("Cbo not found"));
        }

        if (employeeForeignerRequestDto.getPositionId() != null) {
            position = positionRepository.findById(employeeForeignerRequestDto.getPositionId())
                    .orElseThrow(() -> new NotFoundException("Position not found"));
        }
        
        employeeForeigner.setPis(employeeForeignerRequestDto.getPis() != null ? employeeForeignerRequestDto.getPis() : employeeForeigner.getPis());
        employeeForeigner.setMaritalStatus(employeeForeignerRequestDto.getMaritalStatus() != null ? employeeForeignerRequestDto.getMaritalStatus() : employeeForeigner.getMaritalStatus());
        employeeForeigner.setContractType(employeeForeignerRequestDto.getContractType() != null ? employeeForeignerRequestDto.getContractType() : employeeForeigner.getContractType());
        employeeForeigner.setCep(employeeForeignerRequestDto.getCep() != null ? employeeForeignerRequestDto.getCep() : employeeForeigner.getCep());
        employeeForeigner.setName(employeeForeignerRequestDto.getName() != null ? employeeForeignerRequestDto.getName() : employeeForeigner.getName());
        employeeForeigner.setSurname(employeeForeignerRequestDto.getSurname() != null ? employeeForeignerRequestDto.getSurname() : employeeForeigner.getSurname());
        employeeForeigner.setAddress(employeeForeignerRequestDto.getAddress() != null ? employeeForeignerRequestDto.getAddress() : employeeForeigner.getAddress());
        employeeForeigner.setAddressLine2(employeeForeignerRequestDto.getAddressLine2() != null ? employeeForeignerRequestDto.getAddressLine2() : employeeForeigner.getAddressLine2());
        employeeForeigner.setCountry(employeeForeignerRequestDto.getCountry() != null ? employeeForeignerRequestDto.getCountry() : employeeForeigner.getCountry());
        employeeForeigner.setAcronym(employeeForeignerRequestDto.getAcronym() != null ? employeeForeignerRequestDto.getAcronym() : employeeForeigner.getAcronym());
        employeeForeigner.setState(employeeForeignerRequestDto.getState() != null ? employeeForeignerRequestDto.getState() : employeeForeigner.getState());
        employeeForeigner.setBirthDate(employeeForeignerRequestDto.getBirthDate() != null ? employeeForeignerRequestDto.getBirthDate() : employeeForeigner.getBirthDate());
        employeeForeigner.setCity(employeeForeignerRequestDto.getCity() != null ? employeeForeignerRequestDto.getCity() : employeeForeigner.getCity());
        employeeForeigner.setPostalCode(employeeForeignerRequestDto.getPostalCode() != null ? employeeForeignerRequestDto.getPostalCode() : employeeForeigner.getPostalCode());
        employeeForeigner.setGender(employeeForeignerRequestDto.getGender() != null ? employeeForeignerRequestDto.getGender() : employeeForeigner.getGender());
        employeeForeigner.setPosition(employeeForeignerRequestDto.getPositionId() != null
                ? position
                : employeeForeigner.getPosition());
        employeeForeigner.setRegistration(employeeForeignerRequestDto.getRegistration() != null ? employeeForeignerRequestDto.getRegistration() : employeeForeigner.getRegistration());
        employeeForeigner.setSalary(employeeForeignerRequestDto.getSalary() != null ? employeeForeignerRequestDto.getSalary() : employeeForeigner.getSalary());
        employeeForeigner.setCellphone(employeeForeignerRequestDto.getCellphone() != null ? employeeForeignerRequestDto.getCellphone() : employeeForeigner.getCellphone());
        employeeForeigner.setPlatformAccess(employeeForeignerRequestDto.getPlatformAccess() != null ? employeeForeignerRequestDto.getPlatformAccess() : employeeForeigner.getPlatformAccess());
        employeeForeigner.setTelephone(employeeForeignerRequestDto.getTelephone() != null ? employeeForeignerRequestDto.getTelephone() : employeeForeigner.getTelephone());
        employeeForeigner.setDirectory(employeeForeignerRequestDto.getDirectory() != null ? employeeForeignerRequestDto.getDirectory() : employeeForeigner.getDirectory());
        employeeForeigner.setLevelOfEducation(employeeForeignerRequestDto.getLevelOfEducation() != null ? employeeForeignerRequestDto.getLevelOfEducation() : employeeForeigner.getLevelOfEducation());
        employeeForeigner.setCbo(employeeForeignerRequestDto.getCboId() != null ? cbo : employeeForeigner.getCbo());
        employeeForeigner.setSituation(employeeForeignerRequestDto.getSituation() != null ? employeeForeignerRequestDto.getSituation() : employeeForeigner.getSituation());
        employeeForeigner.setRneRnmFederalPoliceProtocol(employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol() != null ? employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol() : employeeForeigner.getRneRnmFederalPoliceProtocol());
        employeeForeigner.setPassport(employeeForeignerRequestDto.getPassport() != null ? employeeForeignerRequestDto.getPassport() : employeeForeigner.getPassport());
        employeeForeigner.setBrazilEntryDate(employeeForeignerRequestDto.getBrazilEntryDate() != null ? employeeForeignerRequestDto.getBrazilEntryDate() : employeeForeigner.getBrazilEntryDate());
//        employeeForeigner.setContracts(employeeForeignerRequestDto.getIdContracts() != null ? contracts : employeeForeigner.getContracts());
        
        EmployeeForeigner savedEmployeeForeigner = employeeForeignerRepository.save(employeeForeigner);

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .idEmployee(employeeForeigner.getIdEmployee())
                .pis(savedEmployeeForeigner.getPis())
                .maritalStatus(savedEmployeeForeigner.getMaritalStatus())
                .contractType(savedEmployeeForeigner.getContractType())
                .cep(savedEmployeeForeigner.getCep())
                .name(savedEmployeeForeigner.getName())
                .surname(savedEmployeeForeigner.getSurname())
                .address(savedEmployeeForeigner.getAddress())
                .country(savedEmployeeForeigner.getCountry())
                .acronym(savedEmployeeForeigner.getAcronym())
                .state(savedEmployeeForeigner.getState())
                .birthDate(savedEmployeeForeigner.getBirthDate())
                .city(savedEmployeeForeigner.getCity())
                .addressLine2(savedEmployeeForeigner.getAddressLine2())
                .postalCode(savedEmployeeForeigner.getPostalCode())
                .gender(savedEmployeeForeigner.getGender())
                .positionId(savedEmployeeForeigner.getPosition() != null
                        ? savedEmployeeForeigner.getPosition().getId()
                        : null)
                .position(savedEmployeeForeigner.getPosition() != null
                        ? savedEmployeeForeigner.getPosition().getTitle()
                        : null)
                .registration(savedEmployeeForeigner.getRegistration())
                .salary(savedEmployeeForeigner.getSalary())
                .cellphone(savedEmployeeForeigner.getCellphone())
                .platformAccess(savedEmployeeForeigner.getPlatformAccess())
                .telephone(savedEmployeeForeigner.getTelephone())
                .directory(savedEmployeeForeigner.getDirectory())
                .levelOfEducation(savedEmployeeForeigner.getLevelOfEducation())
                .cboId(savedEmployeeForeigner.getCbo().getId())
                .situation(savedEmployeeForeigner.getSituation())
                .rneRnmFederalPoliceProtocol(savedEmployeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(savedEmployeeForeigner.getBrazilEntryDate())
                .passport(savedEmployeeForeigner.getPassport())
                .branch(savedEmployeeForeigner.getBranch().getIdBranch())
                .supplier(savedEmployeeForeigner.getSupplier().getIdProvider())
                .subcontract(savedEmployeeForeigner.getSubcontract().getIdProvider())
                .contracts(savedEmployeeForeigner.getContractEmployees().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getContract() != null
                                                ? contract.getContract().getIdContract()
                                                : null)
                                        .serviceName(contract.getContract() != null
                                                ? contract.getContract().getServiceName()
                                                : null)
                                        .build())
                        .collect(Collectors.toList()))
                .build();
        
        return Optional.of(employeeForeignerResponse);
    }

    @Override
    public void delete(String id) {
        employeeForeignerRepository.deleteById(id);
    }

    @Override
    public String changeProfilePicture(String id, MultipartFile file) throws IOException {
        if (file != null) {
            if (file.getSize() > 1024 * 1024) { // 1 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }
        FileDocument savedFileDocument = null;
        EmployeeForeigner employeeForeigner = employeeForeignerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "employee-pfp");

                if (employeeForeigner.getProfilePicture() != null) {
                    googleCloudService.deleteFile(employeeForeigner.getProfilePicture().getUrl());
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
            employeeForeigner.setProfilePicture(savedFileDocument);
        }

        employeeForeignerRepository.save(employeeForeigner);

        return "Profile picture updated successfully";
    }
}
