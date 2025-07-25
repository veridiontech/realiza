package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudEmployeeImpl implements CrudEmployee {

    private final EmployeeBrazilianRepository employeeBrazilianRepository;
    private final EmployeeForeignerRepository employeeForeignerRepository;
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final GoogleCloudService googleCloudService;

    @Override
    public Page<EmployeeResponseDto> findAllByEnterprise(String idSearch, Provider.Company company, Pageable pageable) {
        Page<EmployeeBrazilian> employeeBrazilianPage = Page.empty();
        Page<EmployeeForeigner> employeeForeignerPage = Page.empty();
        List<EmployeeResponseDto> combinedResults = new ArrayList<>();

        switch (company) {
            case CLIENT -> {
                employeeBrazilianPage = employeeBrazilianRepository.findAllByBranch_IdBranch(idSearch, pageable);
                employeeForeignerPage = employeeForeignerRepository.findAllByBranch_IdBranch(idSearch, pageable);
            }
            case SUPPLIER -> {
                employeeBrazilianPage = employeeBrazilianRepository.findAllBySupplier_IdProvider(idSearch, pageable);
                employeeForeignerPage = employeeForeignerRepository.findAllBySupplier_IdProvider(idSearch, pageable);
            }
            case SUBCONTRACTOR -> {
                employeeBrazilianPage = employeeBrazilianRepository.findAllBySubcontract_IdProvider(idSearch, pageable);
                employeeForeignerPage = employeeForeignerRepository.findAllBySubcontract_IdProvider(idSearch, pageable);
            }
        }

        combinedResults.addAll(employeeBrazilianPage.map(
                employeeBrazilian -> {
                    String signedUrl = null;
                    if (employeeBrazilian.getProfilePicture() != null) {
                        if (employeeBrazilian.getProfilePicture().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(employeeBrazilian.getProfilePicture().getUrl(), 15);
                        }
                    }

                    return EmployeeResponseDto.builder()
                            .idEmployee(employeeBrazilian.getIdEmployee())
                            .pis(employeeBrazilian.getPis())
                            .maritalStatus(employeeBrazilian.getMaritalStatus())
                            .contractType(employeeBrazilian.getContractType())
                            .cep(employeeBrazilian.getCep())
                            .name(employeeBrazilian.getName())
                            .surname(employeeBrazilian.getSurname())
                            .profilePictureSignedUrl(signedUrl)
                            .address(employeeBrazilian.getAddress())
                            .addressLine2(employeeBrazilian.getAddressLine2())
                            .country(employeeBrazilian.getCountry())
                            .acronym(employeeBrazilian.getAcronym())
                            .state(employeeBrazilian.getState())
                            .birthDate(employeeBrazilian.getBirthDate())
                            .city(employeeBrazilian.getCity())
                            .postalCode(employeeBrazilian.getPostalCode())
                            .gender(employeeBrazilian.getGender())
                            .positionId(employeeBrazilian.getPosition() != null
                                    ? employeeBrazilian.getPosition().getId()
                                    : null)
                            .position(employeeBrazilian.getPosition() != null
                                    ? employeeBrazilian.getPosition().getTitle()
                                    : null)
                            .registration(employeeBrazilian.getRegistration())
                            .salary(employeeBrazilian.getSalary())
                            .cellphone(employeeBrazilian.getCellphone())
                            .platformAccess(employeeBrazilian.getPlatformAccess())
                            .telephone(employeeBrazilian.getTelephone())
                            .directory(employeeBrazilian.getDirectory())
                            .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                            .cboId(employeeBrazilian.getCbo() != null ? employeeBrazilian.getCbo().getId() : null)
                            .cboTitle(employeeBrazilian.getCbo() != null ? employeeBrazilian.getCbo().getTitle() : null)
                            .cboCode(employeeBrazilian.getCbo() != null ? employeeBrazilian.getCbo().getCode() : null)
                            .situation(employeeBrazilian.getSituation())
                            .admissionDate(employeeBrazilian.getAdmissionDate())
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
        ).getContent());

        combinedResults.addAll(employeeForeignerPage.map(
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
                            .cboTitle(employeeForeigner.getCbo() != null ? employeeForeigner.getCbo().getTitle() : null)
                            .cboCode(employeeForeigner.getCbo() != null ? employeeForeigner.getCbo().getCode() : null)
                            .situation(employeeForeigner.getSituation())
                            .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                            .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                            .passport(employeeForeigner.getPassport())
                            .branch(employeeForeigner.getBranch() != null ? employeeForeigner.getBranch().getIdBranch() : null)
                            .supplier(employeeForeigner.getSupplier() != null ? employeeForeigner.getSupplier().getIdProvider() : null)
                            .subcontract(employeeForeigner.getSubcontract() != null ? employeeForeigner.getSubcontract().getIdProvider() : null)
                            .contracts(employeeForeigner.getContracts().stream().map(
                                            contract -> EmployeeResponseDto.ContractDto.builder()
                                                    .idContract(contract.getIdContract())
                                                    .serviceName(contract.getServiceName())
                                                    .build())
                                    .collect(Collectors.toList()))
                            .build();
                }

        ).getContent());

        return new PageImpl<>(combinedResults, pageable, combinedResults.size());
    }

    @Override
    public EmployeeResponseDto findAllSelectedDocuments(String id) {
        return null;
    }

    @Override
    public String updateDocumentRequests(String id, List<String> documentCollection) {
        return "";
    }

    @Override
    public Page<EmployeeResponseDto> findAllByContract(String idContract, Pageable pageable) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        List<Employee> employees = contract.getEmployees();

        List<EmployeeBrazilian> brazilians = new ArrayList<>();
        List<EmployeeForeigner> foreigners = new ArrayList<>();

        for (Employee emp : employees) {
            if (emp instanceof EmployeeBrazilian eb) {
                brazilians.add(eb);
            } else if (emp instanceof EmployeeForeigner ef) {
                foreigners.add(ef);
            }
        }

        List<EmployeeResponseDto> brazilianDtos = brazilians.stream()
                .map(this::convertBrazilianToDto)
                .toList();

        List<EmployeeResponseDto> foreignerDtos = foreigners.stream()
                .map(this::convertForeignerToDto)
                .toList();

        List<EmployeeResponseDto> allDtos = new ArrayList<>();
        allDtos.addAll(brazilianDtos);
        allDtos.addAll(foreignerDtos);

        allDtos.sort(
                Comparator.comparing(EmployeeResponseDto::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(EmployeeResponseDto::getSurname, String.CASE_INSENSITIVE_ORDER)
        );


        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allDtos.size());

        List<EmployeeResponseDto> pageContent;
        if (start > allDtos.size()) {
            pageContent = List.of();
        } else {
            pageContent = allDtos.subList(start, end);
        }

        return new PageImpl<>(pageContent, pageable, allDtos.size());
    }

    @Override
    public EmployeeResponseDto changeSituation(String employeeId, Employee.Situation situation) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        employee.setSituation(situation);
        Employee savedEmployee = employeeRepository.save(employee);
        if (savedEmployee instanceof EmployeeBrazilian employeeBrazilian) {
            return convertBrazilianToDto(employeeBrazilian);
        } else if (savedEmployee instanceof EmployeeForeigner employeeForeigner) {
            return convertForeignerToDto(employeeForeigner);
        } else {
            throw new NotFoundException("Employee not found");
        }
    }

    @Override
    public Boolean checkEmployeeStatus(String employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new NotFoundException("Employee not found");
        }
        return !documentEmployeeRepository.existsBlockingDocs(employeeId);
    }

    private EmployeeResponseDto convertBrazilianToDto(EmployeeBrazilian employeeBrazilian) {
        String signedUrl = null;
        if (employeeBrazilian.getProfilePicture() != null) {
            if (employeeBrazilian.getProfilePicture().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(employeeBrazilian.getProfilePicture().getUrl(), 15);
            }
        }

        return EmployeeResponseDto.builder()
                .idEmployee(employeeBrazilian.getIdEmployee())
                .pis(employeeBrazilian.getPis())
                .maritalStatus(employeeBrazilian.getMaritalStatus())
                .contractType(employeeBrazilian.getContractType())
                .cep(employeeBrazilian.getCep())
                .name(employeeBrazilian.getName())
                .surname(employeeBrazilian.getSurname())
                .profilePictureSignedUrl(signedUrl)
                .address(employeeBrazilian.getAddress())
                .addressLine2(employeeBrazilian.getAddressLine2())
                .country(employeeBrazilian.getCountry())
                .acronym(employeeBrazilian.getAcronym())
                .state(employeeBrazilian.getState())
                .birthDate(employeeBrazilian.getBirthDate())
                .city(employeeBrazilian.getCity())
                .postalCode(employeeBrazilian.getPostalCode())
                .gender(employeeBrazilian.getGender())
                .positionId(employeeBrazilian.getPosition() != null
                        ? employeeBrazilian.getPosition().getId()
                        : null)
                .position(employeeBrazilian.getPosition() != null
                        ? employeeBrazilian.getPosition().getTitle()
                        : null)
                .registration(employeeBrazilian.getRegistration())
                .salary(employeeBrazilian.getSalary())
                .cellphone(employeeBrazilian.getCellphone())
                .platformAccess(employeeBrazilian.getPlatformAccess())
                .telephone(employeeBrazilian.getTelephone())
                .directory(employeeBrazilian.getDirectory())
                .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                .cboId(employeeBrazilian.getCbo() != null ? employeeBrazilian.getCbo().getId() : null)
                .cboTitle(employeeBrazilian.getCbo() != null ? employeeBrazilian.getCbo().getTitle() : null)
                .cboCode(employeeBrazilian.getCbo() != null ? employeeBrazilian.getCbo().getCode() : null)
                .situation(employeeBrazilian.getSituation())
                .admissionDate(employeeBrazilian.getAdmissionDate())
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

    private EmployeeResponseDto convertForeignerToDto(EmployeeForeigner employeeForeigner) {
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
                .cboTitle(employeeForeigner.getCbo() != null
                        ? employeeForeigner.getCbo().getTitle()
                        : null)
                .cboCode(employeeForeigner.getCbo() != null
                        ? employeeForeigner.getCbo().getCode()
                        : null)
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
                .contracts(employeeForeigner.getContracts().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getIdContract())
                                        .serviceName(contract.getServiceName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
