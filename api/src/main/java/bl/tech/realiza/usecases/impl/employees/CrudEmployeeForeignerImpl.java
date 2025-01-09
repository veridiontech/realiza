package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeForeigner;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudEmployeeForeignerImpl implements CrudEmployeeForeigner {

    private final EmployeeForeignerRepository employeeForeignerRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientRepository clientRepository;

    @Override
    public EmployeeResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        EmployeeForeigner newEmployeeForeigner = EmployeeForeigner.builder()
                .pis(employeeForeignerRequestDto.getPis())
                .marital_status(employeeForeignerRequestDto.getMarital_status())
                .contract(employeeForeignerRequestDto.getContract())
                .cep(employeeForeignerRequestDto.getCep())
                .name(employeeForeignerRequestDto.getName())
                .surname(employeeForeignerRequestDto.getSurname())
                .address(employeeForeignerRequestDto.getAddress())
                .country(employeeForeignerRequestDto.getCountry())
                .acronym(employeeForeignerRequestDto.getAcronym())
                .state(employeeForeignerRequestDto.getState())
                .birth_date(employeeForeignerRequestDto.getBirth_date())
                .city(employeeForeignerRequestDto.getCity())
                .postal_code(employeeForeignerRequestDto.getPostal_code())
                .gender(employeeForeignerRequestDto.getGender())
                .position(employeeForeignerRequestDto.getPosition())
                .registration(employeeForeignerRequestDto.getRegistration())
                .salary(employeeForeignerRequestDto.getSalary())
                .cellphone(employeeForeignerRequestDto.getCellphone())
                .platform_access(employeeForeignerRequestDto.getPlatform_access())
                .telephone(employeeForeignerRequestDto.getTelephone())
                .directory(employeeForeignerRequestDto.getDirectory())
                .email(employeeForeignerRequestDto.getEmail())
                .level_of_education(employeeForeignerRequestDto.getLevel_of_education())
                .cbo(employeeForeignerRequestDto.getCbo())
                .rneRnmFederalPoliceProtocol(employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(employeeForeignerRequestDto.getBrazilEntryDate())
                .passport(employeeForeignerRequestDto.getPassport())
                .build();


        if (employeeForeignerRequestDto.getClient() != null) {
            Optional<Client> clientOptional = clientRepository.findById(employeeForeignerRequestDto.getClient());

            Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

            newEmployeeForeigner = EmployeeForeigner.builder()
                    .client(client)
                    .build();
        } else if (employeeForeignerRequestDto.getSupplier() != null) {
            Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(employeeForeignerRequestDto.getSupplier());

            ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

            newEmployeeForeigner = EmployeeForeigner.builder()
                    .supplier(providerSupplier)
                    .build();

        } else if(employeeForeignerRequestDto.getSubcontract() != null) {
            Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(employeeForeignerRequestDto.getSubcontract());

            ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

            newEmployeeForeigner = EmployeeForeigner.builder()
                    .subcontract(providerSubcontractor)
                    .build();
        }

        EmployeeForeigner savedEmployeeForeigner = employeeForeignerRepository.save(newEmployeeForeigner);

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .pis(savedEmployeeForeigner.getPis())
                .marital_status(savedEmployeeForeigner.getMarital_status())
                .contract(savedEmployeeForeigner.getContract())
                .cep(savedEmployeeForeigner.getCep())
                .name(savedEmployeeForeigner.getName())
                .surname(savedEmployeeForeigner.getSurname())
                .address(savedEmployeeForeigner.getAddress())
                .country(savedEmployeeForeigner.getCountry())
                .acronym(savedEmployeeForeigner.getAcronym())
                .state(savedEmployeeForeigner.getState())
                .birth_date(savedEmployeeForeigner.getBirth_date())
                .city(savedEmployeeForeigner.getCity())
                .postal_code(savedEmployeeForeigner.getPostal_code())
                .gender(savedEmployeeForeigner.getGender())
                .position(savedEmployeeForeigner.getPosition())
                .registration(savedEmployeeForeigner.getRegistration())
                .salary(savedEmployeeForeigner.getSalary())
                .cellphone(savedEmployeeForeigner.getCellphone())
                .platform_access(savedEmployeeForeigner.getPlatform_access())
                .telephone(savedEmployeeForeigner.getTelephone())
                .directory(savedEmployeeForeigner.getDirectory())
                .email(savedEmployeeForeigner.getEmail())
                .level_of_education(savedEmployeeForeigner.getLevel_of_education())
                .cbo(savedEmployeeForeigner.getCbo())
                .rneRnmFederalPoliceProtocol(savedEmployeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(savedEmployeeForeigner.getBrazilEntryDate())
                .passport(savedEmployeeForeigner.getPassport())
                .client(savedEmployeeForeigner.getClient().getIdClient())
                .supplier(savedEmployeeForeigner.getSupplier().getId_provider())
                .subcontract(savedEmployeeForeigner.getSubcontract().getId_provider())
                .build();

        return employeeForeignerResponse;
    }

    @Override
    public Optional<EmployeeResponseDto> findOne(String id) {
        Optional<EmployeeForeigner> employeeForeignerOptional = employeeForeignerRepository.findById(id);

        EmployeeForeigner employeeForeigner = employeeForeignerOptional.orElseThrow(() -> new RuntimeException("Employee Foreigner not found"));

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .pis(employeeForeigner.getPis())
                .marital_status(employeeForeigner.getMarital_status())
                .contract(employeeForeigner.getContract())
                .cep(employeeForeigner.getCep())
                .name(employeeForeigner.getName())
                .surname(employeeForeigner.getSurname())
                .address(employeeForeigner.getAddress())
                .country(employeeForeigner.getCountry())
                .acronym(employeeForeigner.getAcronym())
                .state(employeeForeigner.getState())
                .birth_date(employeeForeigner.getBirth_date())
                .city(employeeForeigner.getCity())
                .postal_code(employeeForeigner.getPostal_code())
                .gender(employeeForeigner.getGender())
                .position(employeeForeigner.getPosition())
                .registration(employeeForeigner.getRegistration())
                .salary(employeeForeigner.getSalary())
                .cellphone(employeeForeigner.getCellphone())
                .platform_access(employeeForeigner.getPlatform_access())
                .telephone(employeeForeigner.getTelephone())
                .directory(employeeForeigner.getDirectory())
                .email(employeeForeigner.getEmail())
                .level_of_education(employeeForeigner.getLevel_of_education())
                .cbo(employeeForeigner.getCbo())
                .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                .passport(employeeForeigner.getPassport())
                .client(employeeForeigner.getClient().getIdClient())
                .supplier(employeeForeigner.getSupplier().getId_provider())
                .subcontract(employeeForeigner.getSubcontract().getId_provider())
                .build();

        return Optional.of(employeeForeignerResponse);
    }

    @Override
    public Page<EmployeeResponseDto> findAll(Pageable pageable) {
        Page<EmployeeForeigner> employeeForeignerPage = employeeForeignerRepository.findAll(pageable);

        Page<EmployeeResponseDto> employeeForeignerResponseDtoPage = employeeForeignerPage.map(
                employeeForeigner -> EmployeeResponseDto.builder()
                        .pis(employeeForeigner.getPis())
                        .marital_status(employeeForeigner.getMarital_status())
                        .contract(employeeForeigner.getContract())
                        .cep(employeeForeigner.getCep())
                        .name(employeeForeigner.getName())
                        .surname(employeeForeigner.getSurname())
                        .address(employeeForeigner.getAddress())
                        .country(employeeForeigner.getCountry())
                        .acronym(employeeForeigner.getAcronym())
                        .state(employeeForeigner.getState())
                        .birth_date(employeeForeigner.getBirth_date())
                        .city(employeeForeigner.getCity())
                        .postal_code(employeeForeigner.getPostal_code())
                        .gender(employeeForeigner.getGender())
                        .position(employeeForeigner.getPosition())
                        .registration(employeeForeigner.getRegistration())
                        .salary(employeeForeigner.getSalary())
                        .cellphone(employeeForeigner.getCellphone())
                        .platform_access(employeeForeigner.getPlatform_access())
                        .telephone(employeeForeigner.getTelephone())
                        .directory(employeeForeigner.getDirectory())
                        .email(employeeForeigner.getEmail())
                        .level_of_education(employeeForeigner.getLevel_of_education())
                        .cbo(employeeForeigner.getCbo())
                        .rneRnmFederalPoliceProtocol(employeeForeigner.getRneRnmFederalPoliceProtocol())
                        .brazilEntryDate(employeeForeigner.getBrazilEntryDate())
                        .passport(employeeForeigner.getPassport())
                        .client(employeeForeigner.getClient().getIdClient())
                        .supplier(employeeForeigner.getSupplier().getId_provider())
                        .subcontract(employeeForeigner.getSubcontract().getId_provider())
                        .build()
        );

        return employeeForeignerResponseDtoPage;
    }

    @Override
    public Optional<EmployeeResponseDto> update(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        Optional<EmployeeForeigner> employeeForeignerOptional = employeeForeignerRepository.findById(employeeForeignerRequestDto.getId_employee());

        EmployeeForeigner employeeForeigner = employeeForeignerOptional.orElseThrow(() -> new RuntimeException("Employee Foreigner not found"));

        employeeForeigner.setPis(employeeForeignerRequestDto.getPis() != null ? employeeForeignerRequestDto.getPis() : employeeForeigner.getPis());
        employeeForeigner.setMarital_status(employeeForeignerRequestDto.getMarital_status() != null ? employeeForeignerRequestDto.getMarital_status() : employeeForeigner.getMarital_status());
        employeeForeigner.setContract(employeeForeignerRequestDto.getContract() != null ? employeeForeignerRequestDto.getContract() : employeeForeigner.getContract());
        employeeForeigner.setCep(employeeForeignerRequestDto.getCep() != null ? employeeForeignerRequestDto.getCep() : employeeForeigner.getCep());
        employeeForeigner.setName(employeeForeignerRequestDto.getName() != null ? employeeForeignerRequestDto.getName() : employeeForeigner.getName());
        employeeForeigner.setSurname(employeeForeignerRequestDto.getSurname() != null ? employeeForeignerRequestDto.getSurname() : employeeForeigner.getSurname());
        employeeForeigner.setAddress(employeeForeignerRequestDto.getAddress() != null ? employeeForeignerRequestDto.getAddress() : employeeForeigner.getAddress());
        employeeForeigner.setCountry(employeeForeignerRequestDto.getCountry() != null ? employeeForeignerRequestDto.getCountry() : employeeForeigner.getCountry());
        employeeForeigner.setAcronym(employeeForeignerRequestDto.getAcronym() != null ? employeeForeignerRequestDto.getAcronym() : employeeForeigner.getAcronym());
        employeeForeigner.setState(employeeForeignerRequestDto.getState() != null ? employeeForeignerRequestDto.getState() : employeeForeigner.getState());
        employeeForeigner.setBirth_date(employeeForeignerRequestDto.getBirth_date() != null ? employeeForeignerRequestDto.getBirth_date() : employeeForeigner.getBirth_date());
        employeeForeigner.setCity(employeeForeignerRequestDto.getCity() != null ? employeeForeignerRequestDto.getCity() : employeeForeigner.getCity());
        employeeForeigner.setPostal_code(employeeForeignerRequestDto.getPostal_code() != null ? employeeForeignerRequestDto.getPostal_code() : employeeForeigner.getPostal_code());
        employeeForeigner.setGender(employeeForeignerRequestDto.getGender() != null ? employeeForeignerRequestDto.getGender() : employeeForeigner.getGender());
        employeeForeigner.setPosition(employeeForeignerRequestDto.getPosition() != null ? employeeForeignerRequestDto.getPosition() : employeeForeigner.getPosition());
        employeeForeigner.setRegistration(employeeForeignerRequestDto.getRegistration() != null ? employeeForeignerRequestDto.getRegistration() : employeeForeigner.getRegistration());
        employeeForeigner.setSalary(employeeForeignerRequestDto.getSalary() != null ? employeeForeignerRequestDto.getSalary() : employeeForeigner.getSalary());
        employeeForeigner.setCellphone(employeeForeignerRequestDto.getCellphone() != null ? employeeForeignerRequestDto.getCellphone() : employeeForeigner.getCellphone());
        employeeForeigner.setPlatform_access(employeeForeignerRequestDto.getPlatform_access() != null ? employeeForeignerRequestDto.getPlatform_access() : employeeForeigner.getPlatform_access());
        employeeForeigner.setTelephone(employeeForeignerRequestDto.getTelephone() != null ? employeeForeignerRequestDto.getTelephone() : employeeForeigner.getTelephone());
        employeeForeigner.setDirectory(employeeForeignerRequestDto.getDirectory() != null ? employeeForeignerRequestDto.getDirectory() : employeeForeigner.getDirectory());
        employeeForeigner.setEmail(employeeForeignerRequestDto.getEmail() != null ? employeeForeignerRequestDto.getEmail() : employeeForeigner.getEmail());
        employeeForeigner.setLevel_of_education(employeeForeignerRequestDto.getLevel_of_education() != null ? employeeForeignerRequestDto.getLevel_of_education() : employeeForeigner.getLevel_of_education());
        employeeForeigner.setCbo(employeeForeignerRequestDto.getCbo() != null ? employeeForeignerRequestDto.getCbo() : employeeForeigner.getCbo());
        employeeForeigner.setRneRnmFederalPoliceProtocol(employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol() != null ? employeeForeignerRequestDto.getRneRnmFederalPoliceProtocol() : employeeForeigner.getRneRnmFederalPoliceProtocol());
        employeeForeigner.setPassport(employeeForeignerRequestDto.getPassport() != null ? employeeForeignerRequestDto.getPassport() : employeeForeigner.getPassport());
        employeeForeigner.setBrazilEntryDate(employeeForeignerRequestDto.getBrazilEntryDate() != null ? employeeForeignerRequestDto.getBrazilEntryDate() : employeeForeigner.getBrazilEntryDate());

        EmployeeForeigner savedEmployeeForeigner = employeeForeignerRepository.save(employeeForeigner);

        EmployeeResponseDto employeeForeignerResponse = EmployeeResponseDto.builder()
                .pis(savedEmployeeForeigner.getPis())
                .marital_status(savedEmployeeForeigner.getMarital_status())
                .contract(savedEmployeeForeigner.getContract())
                .cep(savedEmployeeForeigner.getCep())
                .name(savedEmployeeForeigner.getName())
                .surname(savedEmployeeForeigner.getSurname())
                .address(savedEmployeeForeigner.getAddress())
                .country(savedEmployeeForeigner.getCountry())
                .acronym(savedEmployeeForeigner.getAcronym())
                .state(savedEmployeeForeigner.getState())
                .birth_date(savedEmployeeForeigner.getBirth_date())
                .city(savedEmployeeForeigner.getCity())
                .postal_code(savedEmployeeForeigner.getPostal_code())
                .gender(savedEmployeeForeigner.getGender())
                .position(savedEmployeeForeigner.getPosition())
                .registration(savedEmployeeForeigner.getRegistration())
                .salary(savedEmployeeForeigner.getSalary())
                .cellphone(savedEmployeeForeigner.getCellphone())
                .platform_access(savedEmployeeForeigner.getPlatform_access())
                .telephone(savedEmployeeForeigner.getTelephone())
                .directory(savedEmployeeForeigner.getDirectory())
                .email(savedEmployeeForeigner.getEmail())
                .level_of_education(savedEmployeeForeigner.getLevel_of_education())
                .cbo(savedEmployeeForeigner.getCbo())
                .rneRnmFederalPoliceProtocol(savedEmployeeForeigner.getRneRnmFederalPoliceProtocol())
                .brazilEntryDate(savedEmployeeForeigner.getBrazilEntryDate())
                .passport(savedEmployeeForeigner.getPassport())
                .client(savedEmployeeForeigner.getClient().getIdClient())
                .supplier(savedEmployeeForeigner.getSupplier().getId_provider())
                .subcontract(savedEmployeeForeigner.getSubcontract().getId_provider())
                .build();
        
        return Optional.of(employeeForeignerResponse);
    }

    @Override
    public void delete(String id) {
        employeeForeignerRepository.deleteById(id);
    }
}
