package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeBrazilian;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public EmployeeResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        List<Contract> contracts = List.of();
        EmployeeBrazilian newEmployeeBrazilian = null;
        Client client = null;
        ProviderSupplier providerSupplier = null;
        ProviderSubcontractor providerSubcontractor = null;

        if (employeeBrazilianRequestDto.getIdContracts() != null && !employeeBrazilianRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeBrazilianRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new EntityNotFoundException("Contracts not found");
            }
        }

        if (employeeBrazilianRequestDto.getClient() != null) {
            Optional<Client> clientOptional = clientRepository.findById(employeeBrazilianRequestDto.getClient());

            client = clientOptional.orElseThrow(() -> new EntityNotFoundException("Client not found"));

        } else if (employeeBrazilianRequestDto.getSupplier() != null) {
            Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(employeeBrazilianRequestDto.getSupplier());

            providerSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        } else if(employeeBrazilianRequestDto.getSubcontract() != null) {
            Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(employeeBrazilianRequestDto.getSubcontract());

            providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

            if (employeeBrazilianRequestDto.getIdContracts() != null && !employeeBrazilianRequestDto.getIdContracts().isEmpty()) {
                contracts = contractRepository.findAllById(employeeBrazilianRequestDto.getIdContracts());
            }
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
                .email(employeeBrazilianRequestDto.getEmail())
                .levelOfEducation(employeeBrazilianRequestDto.getLevelOfEducation())
                .cbo(employeeBrazilianRequestDto.getCbo())
                .situation(employeeBrazilianRequestDto.getSituation())
                .rg(employeeBrazilianRequestDto.getRg())
                .admissionDate(employeeBrazilianRequestDto.getAdmissionDate())
                .client(client)
                .supplier(providerSupplier)
                .subcontract(providerSubcontractor)
                .contracts(contracts)
                .build();

        EmployeeBrazilian savedEmployeeBrazilian = employeeBrazilianRepository.save(newEmployeeBrazilian);

        EmployeeResponseDto employeeBrazilianResponse = EmployeeResponseDto.builder()
                .idEmployee(savedEmployeeBrazilian.getIdEmployee())
                .pis(savedEmployeeBrazilian.getPis())
                .maritalStatus(savedEmployeeBrazilian.getMaritalStatus())
                .contractType(savedEmployeeBrazilian.getContractType())
                .cep(savedEmployeeBrazilian.getCep())
                .name(savedEmployeeBrazilian.getName())
                .surname(savedEmployeeBrazilian.getSurname())
                .address(savedEmployeeBrazilian.getAddress())
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
                .email(savedEmployeeBrazilian.getEmail())
                .levelOfEducation(savedEmployeeBrazilian.getLevelOfEducation())
                .cbo(savedEmployeeBrazilian.getCbo())
                .situation(savedEmployeeBrazilian.getSituation())
                .rg(savedEmployeeBrazilian.getRg())
                .admissionDate(savedEmployeeBrazilian.getAdmissionDate())
                .client(savedEmployeeBrazilian.getClient() != null ? savedEmployeeBrazilian.getClient().getIdClient() : null)
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
        Optional<EmployeeBrazilian> employeeBrazilianOptional = employeeBrazilianRepository.findById(id);

        EmployeeBrazilian employeeBrazilian = employeeBrazilianOptional.orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        EmployeeResponseDto employeeBrazilianResponse = EmployeeResponseDto.builder()
                .idEmployee(employeeBrazilian.getIdEmployee())
                .pis(employeeBrazilian.getPis())
                .maritalStatus(employeeBrazilian.getMaritalStatus())
                .contractType(employeeBrazilian.getContractType())
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
                .situation(employeeBrazilian.getSituation())
                .rg(employeeBrazilian.getRg())
                .admissionDate(employeeBrazilian.getAdmissionDate())
                .client(employeeBrazilian.getClient() != null ? employeeBrazilian.getClient().getIdClient() : null)
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

        Page<EmployeeResponseDto> employeeBrazilianResponseDtoPage = employeeBrazilianPage.map(
                employeeBrazilian -> EmployeeResponseDto.builder()
                        .idEmployee(employeeBrazilian.getIdEmployee())
                        .pis(employeeBrazilian.getPis())
                        .maritalStatus(employeeBrazilian.getMaritalStatus())
                        .contractType(employeeBrazilian.getContractType())
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
                        .situation(employeeBrazilian.getSituation())
                        .rg(employeeBrazilian.getRg())
                        .admissionDate(employeeBrazilian.getAdmissionDate())
                        .client(employeeBrazilian.getClient() != null ? employeeBrazilian.getClient().getIdClient() : null)
                        .supplier(employeeBrazilian.getSupplier() != null ? employeeBrazilian.getSupplier().getIdProvider() : null)
                        .subcontract(employeeBrazilian.getSubcontract() != null ? employeeBrazilian.getSubcontract().getIdProvider() : null)
                        .contracts(employeeBrazilian.getContracts().stream().map(
                                        contract -> EmployeeResponseDto.ContractDto.builder()
                                                .idContract(contract.getIdContract())
                                                .serviceName(contract.getServiceName())
                                                .build())
                                .collect(Collectors.toList()))
                        .build()
        );

        return employeeBrazilianResponseDtoPage;
    }

    @Override
    public Optional<EmployeeResponseDto> update(String id, EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        List<Contract> contracts = List.of();

        Optional<EmployeeBrazilian> employeeBrazilianOptional = employeeBrazilianRepository.findById(id);

        EmployeeBrazilian employeeBrazilian = employeeBrazilianOptional.orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (employeeBrazilianRequestDto.getIdContracts() != null && !employeeBrazilianRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeBrazilianRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new EntityNotFoundException("Contracts not found");
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
        employeeBrazilian.setGender(employeeBrazilianRequestDto.getGender() != null ? employeeBrazilianRequestDto.getGender() : employeeBrazilian.getGender());
        employeeBrazilian.setPosition(employeeBrazilianRequestDto.getPosition() != null ? employeeBrazilianRequestDto.getPosition() : employeeBrazilian.getPosition());
        employeeBrazilian.setRegistration(employeeBrazilianRequestDto.getRegistration() != null ? employeeBrazilianRequestDto.getRegistration() : employeeBrazilian.getRegistration());
        employeeBrazilian.setSalary(employeeBrazilianRequestDto.getSalary() != null ? employeeBrazilianRequestDto.getSalary() : employeeBrazilian.getSalary());
        employeeBrazilian.setCellphone(employeeBrazilianRequestDto.getCellphone() != null ? employeeBrazilianRequestDto.getCellphone() : employeeBrazilian.getCellphone());
        employeeBrazilian.setPlatformAccess(employeeBrazilianRequestDto.getPlatformAccess() != null ? employeeBrazilianRequestDto.getPlatformAccess() : employeeBrazilian.getPlatformAccess());
        employeeBrazilian.setTelephone(employeeBrazilianRequestDto.getTelephone() != null ? employeeBrazilianRequestDto.getTelephone() : employeeBrazilian.getTelephone());
        employeeBrazilian.setDirectory(employeeBrazilianRequestDto.getDirectory() != null ? employeeBrazilianRequestDto.getDirectory() : employeeBrazilian.getDirectory());
        employeeBrazilian.setEmail(employeeBrazilianRequestDto.getEmail() != null ? employeeBrazilianRequestDto.getEmail() : employeeBrazilian.getEmail());
        employeeBrazilian.setLevelOfEducation(employeeBrazilianRequestDto.getLevelOfEducation() != null ? employeeBrazilianRequestDto.getLevelOfEducation() : employeeBrazilian.getLevelOfEducation());
        employeeBrazilian.setCbo(employeeBrazilianRequestDto.getCbo() != null ? employeeBrazilianRequestDto.getCbo() : employeeBrazilian.getCbo());
        employeeBrazilian.setSituation(employeeBrazilianRequestDto.getSituation() != null ? employeeBrazilianRequestDto.getSituation() : employeeBrazilian.getSituation());
        employeeBrazilian.setRg(employeeBrazilianRequestDto.getRg() != null ? employeeBrazilianRequestDto.getRg() : employeeBrazilian.getRg());
        employeeBrazilian.setAdmissionDate(employeeBrazilianRequestDto.getAdmissionDate() != null ? employeeBrazilianRequestDto.getAdmissionDate() : employeeBrazilian.getAdmissionDate());
        employeeBrazilian.setIsActive(employeeBrazilianRequestDto.getIsActive() != null ? employeeBrazilianRequestDto.getIsActive() : employeeBrazilian.getIsActive());
        employeeBrazilian.setContracts(employeeBrazilianRequestDto.getIdContracts() != null ? contracts : employeeBrazilian.getContracts());

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
                .email(savedEmployeeBrazilian.getEmail())
                .levelOfEducation(savedEmployeeBrazilian.getLevelOfEducation())
                .cbo(savedEmployeeBrazilian.getCbo())
                .situation(savedEmployeeBrazilian.getSituation())
                .rg(savedEmployeeBrazilian.getRg())
                .admissionDate(savedEmployeeBrazilian.getAdmissionDate())
                .client(savedEmployeeBrazilian.getClient() != null ? savedEmployeeBrazilian.getClient().getIdClient() : null)
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
}
