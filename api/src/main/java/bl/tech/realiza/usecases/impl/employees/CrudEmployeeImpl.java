package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudEmployeeImpl implements CrudEmployee {

    private final EmployeeRepository employeeRepository;
    private final EmployeeBrazilianRepository employeeBrazilianRepository;
    private final EmployeeForeignerRepository employeeForeignerRepository;

    @Override
    public Page<EmployeeResponseDto> findAllByEnterprise(Pageable pageable, EmailRequestDto.Company company, String idSearch) {
        Page<EmployeeBrazilian> employeeBrazilianPage = Page.empty();
        Page<EmployeeForeigner> employeeForeignerPage = Page.empty();
        Page<EmployeeResponseDto> employeeResponseDtoPage = null;

        switch (company) {
            case CLIENT -> {
                employeeBrazilianPage = employeeBrazilianRepository.findAllByClient_IdClient(idSearch, pageable);
                employeeForeignerPage = employeeForeignerRepository.findAllByClient_IdClient(idSearch, pageable);
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

        /*employeeResponseDtoPage = employeeBrazilianPage.map(
                employeeBrazilian -> EmployeeResponseDto.builder()
                        .idEmployee(employeeBrazilian.getIdEmployee())
                        .pis(employeeBrazilian.getPis())
                        .maritalStatus(employeeBrazilian.getMaritalStatus())
                        .contract(employeeBrazilian.getContract())
                        .cep(employeeBrazilian.getCep())
                        .name(employeeBrazilian.getName())
                        .surname(employeeBrazilian.getSurname())
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
                        .email(employeeBrazilian.getEmail())
                        .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                        .cbo(employeeBrazilian.getCbo())
                        .rg(employeeBrazilian.getRg())
                        .admissionDate(employeeBrazilian.getAdmissionDate())
                        .client(employeeBrazilian.getClient() != null ? employeeBrazilian.getClient().getIdClient() : null)
                        .supplier(employeeBrazilian.getSupplier() != null ? employeeBrazilian.getSupplier().getIdProvider() : null)
                        .subcontract(employeeBrazilian.getSubcontract() != null ? employeeBrazilian.getSubcontract().getIdProvider() : null)
                        .build()
        );

        employeeResponseDtoPage = employeeForeignerPage.map(
                employeeForeigner -> EmployeeResponseDto.builder()
                        .idEmployee(employeeForeigner.getIdEmployee())
                        .pis(employeeForeigner.getPis())
                        .maritalStatus(employeeForeigner.getMaritalStatus())
                        .contract(employeeForeigner.getContract())
                        .cep(employeeForeigner.getCep())
                        .name(employeeForeigner.getName())
                        .surname(employeeForeigner.getSurname())
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
                        .email(employeeForeigner.getEmail())
                        .levelOfEducation(employeeForeigner.getLevelOfEducation())
                        .cbo(employeeForeigner.getCbo())
                        .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                        .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                        .passport(employeeForeigner.getPassport())
                        .client(employeeForeigner.getClient().getIdClient())
                        .supplier(employeeForeigner.getSupplier().getIdProvider())
                        .subcontract(employeeForeigner.getSubcontract().getIdProvider())
                        .build()
        );

        return employeeResponseDtoPage;*/

        List<EmployeeResponseDto> combinedResults = new ArrayList<>();

        combinedResults.addAll(employeeBrazilianPage.map(
                employeeBrazilian -> EmployeeResponseDto.builder()
                        .idEmployee(employeeBrazilian.getIdEmployee())
                        .pis(employeeBrazilian.getPis())
                        .maritalStatus(employeeBrazilian.getMaritalStatus())
                        .contract(employeeBrazilian.getContract())
                        .cep(employeeBrazilian.getCep())
                        .name(employeeBrazilian.getName())
                        .surname(employeeBrazilian.getSurname())
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
                        .email(employeeBrazilian.getEmail())
                        .levelOfEducation(employeeBrazilian.getLevelOfEducation())
                        .cbo(employeeBrazilian.getCbo())
                        .rg(employeeBrazilian.getRg())
                        .admissionDate(employeeBrazilian.getAdmissionDate())
                        .client(employeeBrazilian.getClient() != null ? employeeBrazilian.getClient().getIdClient() : null)
                        .supplier(employeeBrazilian.getSupplier() != null ? employeeBrazilian.getSupplier().getIdProvider() : null)
                        .subcontract(employeeBrazilian.getSubcontract() != null ? employeeBrazilian.getSubcontract().getIdProvider() : null)
                        .build()
        ).getContent());

        combinedResults.addAll(employeeForeignerPage.map(
                employeeForeigner -> EmployeeResponseDto.builder()
                        .idEmployee(employeeForeigner.getIdEmployee())
                        .pis(employeeForeigner.getPis())
                        .maritalStatus(employeeForeigner.getMaritalStatus())
                        .contract(employeeForeigner.getContract())
                        .cep(employeeForeigner.getCep())
                        .name(employeeForeigner.getName())
                        .surname(employeeForeigner.getSurname())
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
                        .email(employeeForeigner.getEmail())
                        .levelOfEducation(employeeForeigner.getLevelOfEducation())
                        .cbo(employeeForeigner.getCbo())
                        .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                        .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                        .passport(employeeForeigner.getPassport())
                        .client(employeeForeigner.getClient() != null ? employeeForeigner.getClient().getIdClient() : null)
                        .supplier(employeeForeigner.getSupplier() != null ? employeeForeigner.getSupplier().getIdProvider() : null)
                        .subcontract(employeeForeigner.getSubcontract() != null ? employeeForeigner.getSubcontract().getIdProvider() : null)
                        .build()
        ).getContent());

        return new PageImpl<>(combinedResults, pageable, combinedResults.size());
    }
}
