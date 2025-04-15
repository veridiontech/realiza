package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployee;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudEmployeeImpl implements CrudEmployee {

    private final EmployeeBrazilianRepository employeeBrazilianRepository;
    private final EmployeeForeignerRepository employeeForeignerRepository;
    private final FileRepository fileRepository;

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
                            .gender(employeeBrazilian.getGender())
                            .position(employeeBrazilian.getPosition())
                            .registration(employeeBrazilian.getRegistration())
                            .salary(employeeBrazilian.getSalary())
                            .cellphone(employeeBrazilian.getCellphone())
                            .platformAccess(employeeBrazilian.getPlatformAccess())
                            .telephone(employeeBrazilian.getTelephone())
                            .directory(employeeBrazilian.getDirectory())
                            .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                            .cbo(employeeBrazilian.getCbo())
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
                    FileDocument fileDocument = null;
                    if (employeeForeigner.getProfilePicture() != null && !employeeForeigner.getProfilePicture().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(employeeForeigner.getProfilePicture()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }
                    return EmployeeResponseDto.builder()
                            .idEmployee(employeeForeigner.getIdEmployee())
                            .pis(employeeForeigner.getPis())
                            .maritalStatus(employeeForeigner.getMaritalStatus())
                            .contractType(employeeForeigner.getContractType())
                            .cep(employeeForeigner.getCep())
                            .name(employeeForeigner.getName())
                            .surname(employeeForeigner.getSurname())
                            .profilePictureData(fileDocument != null ? fileDocument.getData() : null)
                            .address(employeeForeigner.getAddress())
                            .country(employeeForeigner.getCountry())
                            .acronym(employeeForeigner.getAcronym())
                            .state(employeeForeigner.getState())
                            .birthDate(employeeForeigner.getBirthDate())
                            .city(employeeForeigner.getCity())
                            .postalCode(employeeForeigner.getPostalCode())
                            .gender(employeeForeigner.getGender())
                            .position(employeeForeigner.getPosition())
                            .registration(employeeForeigner.getRegistration())
                            .salary(employeeForeigner.getSalary())
                            .cellphone(employeeForeigner.getCellphone())
                            .platformAccess(employeeForeigner.getPlatformAccess())
                            .telephone(employeeForeigner.getTelephone())
                            .directory(employeeForeigner.getDirectory())
                            .levelOfEducation(employeeForeigner.getLevelOfEducation())
                            .cbo(employeeForeigner.getCbo())
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
}
