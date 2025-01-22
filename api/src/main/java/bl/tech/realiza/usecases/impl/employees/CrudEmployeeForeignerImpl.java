package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeForeigner;
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
public class CrudEmployeeForeignerImpl implements CrudEmployeeForeigner {

    private final EmployeeForeignerRepository employeeForeignerRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;

    @Override
    public EmployeeResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        List<Contract> contracts = List.of();
        EmployeeForeigner newEmployeeForeigner = null;

        if (employeeForeignerRequestDto.getIdContracts() != null && !employeeForeignerRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeForeignerRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new EntityNotFoundException("Contracts not found");
            }
        }
        
        if (employeeForeignerRequestDto.getClient() != null) {
            Optional<Client> clientOptional = clientRepository.findById(employeeForeignerRequestDto.getClient());

            Client client = clientOptional.orElseThrow(() -> new EntityNotFoundException("Client not found"));

            newEmployeeForeigner = EmployeeForeigner.builder()
                    .client(client)
                    .build();
        } else if (employeeForeignerRequestDto.getSupplier() != null) {
            Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(employeeForeignerRequestDto.getSupplier());

            ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

            newEmployeeForeigner = EmployeeForeigner.builder()
                    .supplier(providerSupplier)
                    .build();

        } else if(employeeForeignerRequestDto.getSubcontract() != null) {
            Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(employeeForeignerRequestDto.getSubcontract());

            ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

            newEmployeeForeigner = EmployeeForeigner.builder()
                    .subcontract(providerSubcontractor)
                    .build();
        }

        newEmployeeForeigner = EmployeeForeigner.builder()
                .pis(employeeForeignerRequestDto.getPis())
                .maritalStatus(employeeForeignerRequestDto.getMaritalStatus())
                .contractType(employeeForeignerRequestDto.getContractType())
                .cep(employeeForeignerRequestDto.getCep())
                .name(employeeForeignerRequestDto.getName())
                .surname(employeeForeignerRequestDto.getSurname())
                .address(employeeForeignerRequestDto.getAddress())
                .country(employeeForeignerRequestDto.getCountry())
                .acronym(employeeForeignerRequestDto.getAcronym())
                .state(employeeForeignerRequestDto.getState())
                .birthDate(employeeForeignerRequestDto.getBirthDate())
                .city(employeeForeignerRequestDto.getCity())
                .postalCode(employeeForeignerRequestDto.getPostalCode())
                .gender(employeeForeignerRequestDto.getGender())
                .position(employeeForeignerRequestDto.getPosition())
                .registration(employeeForeignerRequestDto.getRegistration())
                .salary(employeeForeignerRequestDto.getSalary())
                .cellphone(employeeForeignerRequestDto.getCellphone())
                .platformAccess(employeeForeignerRequestDto.getPlatformAccess())
                .telephone(employeeForeignerRequestDto.getTelephone())
                .directory(employeeForeignerRequestDto.getDirectory())
                .email(employeeForeignerRequestDto.getEmail())
                .levelOfEducation(employeeForeignerRequestDto.getLevelOfEducation())
                .cbo(employeeForeignerRequestDto.getCbo())
                .rneRnmFederalPoliceProtocol(employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(employeeForeignerRequestDto.getBrazilEntryDate())
                .passport(employeeForeignerRequestDto.getPassport())
                .contracts(contracts)
                .build();

        EmployeeForeigner savedEmployeeForeigner = employeeForeignerRepository.save(newEmployeeForeigner);

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .idEmployee(savedEmployeeForeigner.getIdEmployee())
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
                .postalCode(savedEmployeeForeigner.getPostalCode())
                .gender(savedEmployeeForeigner.getGender())
                .position(savedEmployeeForeigner.getPosition())
                .registration(savedEmployeeForeigner.getRegistration())
                .salary(savedEmployeeForeigner.getSalary())
                .cellphone(savedEmployeeForeigner.getCellphone())
                .platformAccess(savedEmployeeForeigner.getPlatformAccess())
                .telephone(savedEmployeeForeigner.getTelephone())
                .directory(savedEmployeeForeigner.getDirectory())
                .email(savedEmployeeForeigner.getEmail())
                .levelOfEducation(savedEmployeeForeigner.getLevelOfEducation())
                .cbo(savedEmployeeForeigner.getCbo())
                .rneRnmFederalPoliceProtocol(savedEmployeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(savedEmployeeForeigner.getBrazilEntryDate())
                .passport(savedEmployeeForeigner.getPassport())
                .client(savedEmployeeForeigner.getClient().getIdClient())
                .supplier(savedEmployeeForeigner.getSupplier().getIdProvider())
                .subcontract(savedEmployeeForeigner.getSubcontract().getIdProvider())
                .contracts(savedEmployeeForeigner.getContracts().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getIdContract())
                                        .serviceName(contract.getServiceName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        return employeeForeignerResponse;
    }

    @Override
    public Optional<EmployeeResponseDto> findOne(String id) {
        Optional<EmployeeForeigner> employeeForeignerOptional = employeeForeignerRepository.findById(id);

        EmployeeForeigner employeeForeigner = employeeForeignerOptional.orElseThrow(() -> new EntityNotFoundException("Employee Foreigner not found"));

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .idEmployee(employeeForeigner.getIdEmployee())
                .pis(employeeForeigner.getPis())
                .maritalStatus(employeeForeigner.getMaritalStatus())
                .contractType(employeeForeigner.getContractType())
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
                .contracts(employeeForeigner.getContracts().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getIdContract())
                                        .serviceName(contract.getServiceName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        return Optional.of(employeeForeignerResponse);
    }

    @Override
    public Page<EmployeeResponseDto> findAll(Pageable pageable) {
        Page<EmployeeForeigner> employeeForeignerPage = employeeForeignerRepository.findAll(pageable);

        Page<EmployeeResponseDto> employeeForeignerResponseDtoPage = employeeForeignerPage.map(
                employeeForeigner -> EmployeeResponseDto.builder()
                        .idEmployee(employeeForeigner.getIdEmployee())
                        .pis(employeeForeigner.getPis())
                        .maritalStatus(employeeForeigner.getMaritalStatus())
                        .contractType(employeeForeigner.getContractType())
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
                        .contracts(employeeForeigner.getContracts().stream().map(
                                        contract -> EmployeeResponseDto.ContractDto.builder()
                                                .idContract(contract.getIdContract())
                                                .serviceName(contract.getServiceName())
                                                .build())
                                .collect(Collectors.toList()))
                        .build()
        );

        return employeeForeignerResponseDtoPage;
    }

    @Override
    public Optional<EmployeeResponseDto> update(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        List<Contract> contracts = List.of();
        
        Optional<EmployeeForeigner> employeeForeignerOptional = employeeForeignerRepository.findById(employeeForeignerRequestDto.getIdEmployee());

        EmployeeForeigner employeeForeigner = employeeForeignerOptional.orElseThrow(() -> new EntityNotFoundException("Employee Foreigner not found"));

        if (employeeForeignerRequestDto.getIdContracts() != null && !employeeForeignerRequestDto.getIdContracts().isEmpty()) {
            contracts = contractRepository.findAllById(employeeForeignerRequestDto.getIdContracts());
            if (contracts.isEmpty()) {
                throw new EntityNotFoundException("Contracts not found");
            }
        }
        
        employeeForeigner.setPis(employeeForeignerRequestDto.getPis() != null ? employeeForeignerRequestDto.getPis() : employeeForeigner.getPis());
        employeeForeigner.setMaritalStatus(employeeForeignerRequestDto.getMaritalStatus() != null ? employeeForeignerRequestDto.getMaritalStatus() : employeeForeigner.getMaritalStatus());
        employeeForeigner.setContractType(employeeForeignerRequestDto.getContractType() != null ? employeeForeignerRequestDto.getContractType() : employeeForeigner.getContractType());
        employeeForeigner.setCep(employeeForeignerRequestDto.getCep() != null ? employeeForeignerRequestDto.getCep() : employeeForeigner.getCep());
        employeeForeigner.setName(employeeForeignerRequestDto.getName() != null ? employeeForeignerRequestDto.getName() : employeeForeigner.getName());
        employeeForeigner.setSurname(employeeForeignerRequestDto.getSurname() != null ? employeeForeignerRequestDto.getSurname() : employeeForeigner.getSurname());
        employeeForeigner.setAddress(employeeForeignerRequestDto.getAddress() != null ? employeeForeignerRequestDto.getAddress() : employeeForeigner.getAddress());
        employeeForeigner.setCountry(employeeForeignerRequestDto.getCountry() != null ? employeeForeignerRequestDto.getCountry() : employeeForeigner.getCountry());
        employeeForeigner.setAcronym(employeeForeignerRequestDto.getAcronym() != null ? employeeForeignerRequestDto.getAcronym() : employeeForeigner.getAcronym());
        employeeForeigner.setState(employeeForeignerRequestDto.getState() != null ? employeeForeignerRequestDto.getState() : employeeForeigner.getState());
        employeeForeigner.setBirthDate(employeeForeignerRequestDto.getBirthDate() != null ? employeeForeignerRequestDto.getBirthDate() : employeeForeigner.getBirthDate());
        employeeForeigner.setCity(employeeForeignerRequestDto.getCity() != null ? employeeForeignerRequestDto.getCity() : employeeForeigner.getCity());
        employeeForeigner.setPostalCode(employeeForeignerRequestDto.getPostalCode() != null ? employeeForeignerRequestDto.getPostalCode() : employeeForeigner.getPostalCode());
        employeeForeigner.setGender(employeeForeignerRequestDto.getGender() != null ? employeeForeignerRequestDto.getGender() : employeeForeigner.getGender());
        employeeForeigner.setPosition(employeeForeignerRequestDto.getPosition() != null ? employeeForeignerRequestDto.getPosition() : employeeForeigner.getPosition());
        employeeForeigner.setRegistration(employeeForeignerRequestDto.getRegistration() != null ? employeeForeignerRequestDto.getRegistration() : employeeForeigner.getRegistration());
        employeeForeigner.setSalary(employeeForeignerRequestDto.getSalary() != null ? employeeForeignerRequestDto.getSalary() : employeeForeigner.getSalary());
        employeeForeigner.setCellphone(employeeForeignerRequestDto.getCellphone() != null ? employeeForeignerRequestDto.getCellphone() : employeeForeigner.getCellphone());
        employeeForeigner.setPlatformAccess(employeeForeignerRequestDto.getPlatformAccess() != null ? employeeForeignerRequestDto.getPlatformAccess() : employeeForeigner.getPlatformAccess());
        employeeForeigner.setTelephone(employeeForeignerRequestDto.getTelephone() != null ? employeeForeignerRequestDto.getTelephone() : employeeForeigner.getTelephone());
        employeeForeigner.setDirectory(employeeForeignerRequestDto.getDirectory() != null ? employeeForeignerRequestDto.getDirectory() : employeeForeigner.getDirectory());
        employeeForeigner.setEmail(employeeForeignerRequestDto.getEmail() != null ? employeeForeignerRequestDto.getEmail() : employeeForeigner.getEmail());
        employeeForeigner.setLevelOfEducation(employeeForeignerRequestDto.getLevelOfEducation() != null ? employeeForeignerRequestDto.getLevelOfEducation() : employeeForeigner.getLevelOfEducation());
        employeeForeigner.setCbo(employeeForeignerRequestDto.getCbo() != null ? employeeForeignerRequestDto.getCbo() : employeeForeigner.getCbo());
        employeeForeigner.setRneRnmFederalPoliceProtocol(employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol() != null ? employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol() : employeeForeigner.getRneRnmFederalPoliceProtocol());
        employeeForeigner.setPassport(employeeForeignerRequestDto.getPassport() != null ? employeeForeignerRequestDto.getPassport() : employeeForeigner.getPassport());
        employeeForeigner.setBrazilEntryDate(employeeForeignerRequestDto.getBrazilEntryDate() != null ? employeeForeignerRequestDto.getBrazilEntryDate() : employeeForeigner.getBrazilEntryDate());
        employeeForeigner.setIsActive(employeeForeignerRequestDto.getIsActive() != null ? employeeForeignerRequestDto.getIsActive() : employeeForeigner.getIsActive());
        employeeForeigner.setContracts(employeeForeignerRequestDto.getIdContracts() != null ? contracts : employeeForeigner.getContracts());
        
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
                .postalCode(savedEmployeeForeigner.getPostalCode())
                .gender(savedEmployeeForeigner.getGender())
                .position(savedEmployeeForeigner.getPosition())
                .registration(savedEmployeeForeigner.getRegistration())
                .salary(savedEmployeeForeigner.getSalary())
                .cellphone(savedEmployeeForeigner.getCellphone())
                .platformAccess(savedEmployeeForeigner.getPlatformAccess())
                .telephone(savedEmployeeForeigner.getTelephone())
                .directory(savedEmployeeForeigner.getDirectory())
                .email(savedEmployeeForeigner.getEmail())
                .levelOfEducation(savedEmployeeForeigner.getLevelOfEducation())
                .cbo(savedEmployeeForeigner.getCbo())
                .rneRnmFederalPoliceProtocol(savedEmployeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(savedEmployeeForeigner.getBrazilEntryDate())
                .passport(savedEmployeeForeigner.getPassport())
                .client(savedEmployeeForeigner.getClient().getIdClient())
                .supplier(savedEmployeeForeigner.getSupplier().getIdProvider())
                .subcontract(savedEmployeeForeigner.getSubcontract().getIdProvider())
                .contracts(savedEmployeeForeigner.getContracts().stream().map(
                                contract -> EmployeeResponseDto.ContractDto.builder()
                                        .idContract(contract.getIdContract())
                                        .serviceName(contract.getServiceName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
        
        return Optional.of(employeeForeignerResponse);
    }

    @Override
    public void delete(String id) {
        employeeForeignerRepository.deleteById(id);
    }
}
