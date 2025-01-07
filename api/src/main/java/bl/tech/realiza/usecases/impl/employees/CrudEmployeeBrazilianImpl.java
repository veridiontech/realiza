package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeBrazilianRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeBrazilian;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudEmployeeBrazilianImpl implements CrudEmployeeBrazilian {

    private final EmployeeBrazilianRepository employeeBrazilianRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientRepository clientRepository;

    @Override
    public EmployeeBrazilianResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {

        EmployeeBrazilian newEmployeeBrazilian = EmployeeBrazilian.builder()
                .pis(employeeBrazilianRequestDto.getPis())
                .marital_status(employeeBrazilianRequestDto.getMarital_status())
                .contract(employeeBrazilianRequestDto.getContract())
                .cep(employeeBrazilianRequestDto.getCep())
                .name(employeeBrazilianRequestDto.getName())
                .surname(employeeBrazilianRequestDto.getSurname())
                .address(employeeBrazilianRequestDto.getAddress())
                .country(employeeBrazilianRequestDto.getCountry())
                .acronym(employeeBrazilianRequestDto.getAcronym())
                .state(employeeBrazilianRequestDto.getState())
                .birth_date(employeeBrazilianRequestDto.getBirth_date())
                .city(employeeBrazilianRequestDto.getCity())
                .postal_code(employeeBrazilianRequestDto.getPostal_code())
                .gender(employeeBrazilianRequestDto.getGender())
                .position(employeeBrazilianRequestDto.getPosition())
                .registration(employeeBrazilianRequestDto.getRegistration())
                .salary(employeeBrazilianRequestDto.getSalary())
                .cellphone(employeeBrazilianRequestDto.getCellphone())
                .platform_access(employeeBrazilianRequestDto.getPlatform_access())
                .telephone(employeeBrazilianRequestDto.getTelephone())
                .directory(employeeBrazilianRequestDto.getDirectory())
                .email(employeeBrazilianRequestDto.getEmail())
                .level_of_education(employeeBrazilianRequestDto.getLevel_of_education())
                .cbo(employeeBrazilianRequestDto.getCbo())
                .rg(employeeBrazilianRequestDto.getRg())
                .admission_date(employeeBrazilianRequestDto.getAdmission_date())
                .build();


        if (employeeBrazilianRequestDto.getClient() != null) {
            Optional<Client> clientOptional = clientRepository.findById(employeeBrazilianRequestDto.getClient());

            Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

            newEmployeeBrazilian = EmployeeBrazilian.builder()
                    .client(client)
                    .build();
        } else if (employeeBrazilianRequestDto.getSupplier() != null) {
            Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(employeeBrazilianRequestDto.getSupplier());

            ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

            newEmployeeBrazilian = EmployeeBrazilian.builder()
                    .supplier(providerSupplier)
                    .build();

        } else if(employeeBrazilianRequestDto.getSubcontract() != null) {
            Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(employeeBrazilianRequestDto.getSubcontract());

            ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

            newEmployeeBrazilian = EmployeeBrazilian.builder()
                    .subcontract(providerSubcontractor)
                    .build();
        }

        EmployeeBrazilian savedEmployeeBrazilian = employeeBrazilianRepository.save(newEmployeeBrazilian);

        EmployeeBrazilianResponseDto employeeBrazilianResponse = EmployeeBrazilianResponseDto.builder()
                .pis(savedEmployeeBrazilian.getPis())
                .marital_status(savedEmployeeBrazilian.getMarital_status())
                .contract(savedEmployeeBrazilian.getContract())
                .cep(savedEmployeeBrazilian.getCep())
                .name(savedEmployeeBrazilian.getName())
                .surname(savedEmployeeBrazilian.getSurname())
                .address(savedEmployeeBrazilian.getAddress())
                .country(savedEmployeeBrazilian.getCountry())
                .acronym(savedEmployeeBrazilian.getAcronym())
                .state(savedEmployeeBrazilian.getState())
                .birth_date(savedEmployeeBrazilian.getBirth_date())
                .city(savedEmployeeBrazilian.getCity())
                .postal_code(savedEmployeeBrazilian.getPostal_code())
                .gender(savedEmployeeBrazilian.getGender())
                .position(savedEmployeeBrazilian.getPosition())
                .registration(savedEmployeeBrazilian.getRegistration())
                .salary(savedEmployeeBrazilian.getSalary())
                .cellphone(savedEmployeeBrazilian.getCellphone())
                .platform_access(savedEmployeeBrazilian.getPlatform_access())
                .telephone(savedEmployeeBrazilian.getTelephone())
                .directory(savedEmployeeBrazilian.getDirectory())
                .email(savedEmployeeBrazilian.getEmail())
                .level_of_education(savedEmployeeBrazilian.getLevel_of_education())
                .cbo(savedEmployeeBrazilian.getCbo())
                .rg(savedEmployeeBrazilian.getRg())
                .admission_date(savedEmployeeBrazilian.getAdmission_date())
                .client(savedEmployeeBrazilian.getClient().getIdClient())
                .supplier(savedEmployeeBrazilian.getSupplier().getId_provider())
                .subcontract(savedEmployeeBrazilian.getSubcontract().getId_provider())
                .build();

        return employeeBrazilianResponse;
    }

    @Override
    public Optional<EmployeeBrazilianResponseDto> findOne(String id) {

        Optional<EmployeeBrazilian> employeeBrazilianOptional = employeeBrazilianRepository.findById(id);

        EmployeeBrazilian employeeBrazilian = employeeBrazilianOptional.orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeBrazilianResponseDto employeeBrazilianResponse = EmployeeBrazilianResponseDto.builder()
                .pis(employeeBrazilian.getPis())
                .marital_status(employeeBrazilian.getMarital_status())
                .contract(employeeBrazilian.getContract())
                .cep(employeeBrazilian.getCep())
                .name(employeeBrazilian.getName())
                .surname(employeeBrazilian.getSurname())
                .address(employeeBrazilian.getAddress())
                .country(employeeBrazilian.getCountry())
                .acronym(employeeBrazilian.getAcronym())
                .state(employeeBrazilian.getState())
                .birth_date(employeeBrazilian.getBirth_date())
                .city(employeeBrazilian.getCity())
                .postal_code(employeeBrazilian.getPostal_code())
                .gender(employeeBrazilian.getGender())
                .position(employeeBrazilian.getPosition())
                .registration(employeeBrazilian.getRegistration())
                .salary(employeeBrazilian.getSalary())
                .cellphone(employeeBrazilian.getCellphone())
                .platform_access(employeeBrazilian.getPlatform_access())
                .telephone(employeeBrazilian.getTelephone())
                .directory(employeeBrazilian.getDirectory())
                .email(employeeBrazilian.getEmail())
                .level_of_education(employeeBrazilian.getLevel_of_education())
                .cbo(employeeBrazilian.getCbo())
                .rg(employeeBrazilian.getRg())
                .admission_date(employeeBrazilian.getAdmission_date())
                .client(employeeBrazilian.getClient().getIdClient())
                .supplier(employeeBrazilian.getSupplier().getId_provider())
                .subcontract(employeeBrazilian.getSubcontract().getId_provider())
                .build();
        
        return Optional.of(employeeBrazilianResponse);
    }

    @Override
    public Page<EmployeeBrazilianResponseDto> findAll(Pageable pageable) {

        Page<EmployeeBrazilian> employeeBrazilianPage = employeeBrazilianRepository.findAll(pageable);

        Page<EmployeeBrazilianResponseDto> employeeBrazilianResponseDtoPage = employeeBrazilianPage.map(
                employeeBrazilian -> EmployeeBrazilianResponseDto.builder()
                        .pis(employeeBrazilian.getPis())
                        .marital_status(employeeBrazilian.getMarital_status())
                        .contract(employeeBrazilian.getContract())
                        .cep(employeeBrazilian.getCep())
                        .name(employeeBrazilian.getName())
                        .surname(employeeBrazilian.getSurname())
                        .address(employeeBrazilian.getAddress())
                        .country(employeeBrazilian.getCountry())
                        .acronym(employeeBrazilian.getAcronym())
                        .state(employeeBrazilian.getState())
                        .birth_date(employeeBrazilian.getBirth_date())
                        .city(employeeBrazilian.getCity())
                        .postal_code(employeeBrazilian.getPostal_code())
                        .gender(employeeBrazilian.getGender())
                        .position(employeeBrazilian.getPosition())
                        .registration(employeeBrazilian.getRegistration())
                        .salary(employeeBrazilian.getSalary())
                        .cellphone(employeeBrazilian.getCellphone())
                        .platform_access(employeeBrazilian.getPlatform_access())
                        .telephone(employeeBrazilian.getTelephone())
                        .directory(employeeBrazilian.getDirectory())
                        .email(employeeBrazilian.getEmail())
                        .level_of_education(employeeBrazilian.getLevel_of_education())
                        .cbo(employeeBrazilian.getCbo())
                        .rg(employeeBrazilian.getRg())
                        .admission_date(employeeBrazilian.getAdmission_date())
                        .client(employeeBrazilian.getClient().getIdClient())
                        .supplier(employeeBrazilian.getSupplier().getId_provider())
                        .subcontract(employeeBrazilian.getSubcontract().getId_provider())
                        .build()
        );

        return employeeBrazilianResponseDtoPage;
    }

    @Override
    public Optional<EmployeeBrazilianResponseDto> update(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        employeeBrazilianRepository.deleteById(id);
    }
}
