package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.documents.contract.DocumentContractRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContractProviderSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContractProviderSubcontractorImpl implements CrudContractProviderSubcontractor {

    private final ContractProviderSubcontractorRepository contractProviderSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ActivityRepository activityRepository;
    private final RequirementRepository requirementRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final DocumentContractRepository documentContractRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final CrudItemManagementImpl crudItemManagementImpl;
    private final ActivityDocumentRepository activityDocumentRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;

    @Override
    public ContractSubcontractorResponseDto save(ContractSubcontractorPostRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();
        List<DocumentProviderSupplier> documentSupplier = List.of();
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentContract> documentContract = new ArrayList<>(List.of());
        List<DocumentProviderSubcontractor> documentProviderSubcontractor = new ArrayList<>(List.of());

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdContractSupplier())
                .orElseThrow(() -> new NotFoundException("Supplier contract not found"));

        if (!contractProviderSupplier.getSubcontractPermission()) {
            throw new UnprocessableEntityException("Contract can't get subcontracted");
        }

        ProviderSubcontractor newProviderSubcontractor = providerSubcontractorRepository.findByCnpj(contractProviderSubcontractorRequestDto.getProviderDatas().getCnpj())
                .orElseThrow(null);

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));
// depois de terminar subcontratado, verificar erro de get contract supplier pelo id do supplier
        if (contractProviderSubcontractorRequestDto.getHse() && !contractProviderSubcontractorRequestDto.getIdActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getIdActivities());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        activities.forEach(
                activity -> {
                    List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                    activityDocumentsList.forEach(
                            activityDocument -> idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation())
                    );
                }
        );

        if (newProviderSubcontractor == null) {
            newProviderSubcontractor = providerSubcontractorRepository.save(ProviderSubcontractor.builder()
                    .cnpj(contractProviderSubcontractorRequestDto.getProviderDatas().getCnpj())
                    .corporateName(contractProviderSubcontractorRequestDto.getProviderDatas().getCorporateName())
                    .email(contractProviderSubcontractorRequestDto.getProviderDatas().getEmail())
                    .telephone(contractProviderSubcontractorRequestDto.getProviderDatas().getTelephone())
                    .providerSupplier(providerSupplier)
                    .build());
        }

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(ContractProviderSubcontractor.builder()
                .serviceTypeBranch(contractProviderSupplier.getServiceTypeBranch())
                .serviceName(contractProviderSubcontractorRequestDto.getServiceName())
                .contractReference(contractProviderSubcontractorRequestDto.getContractReference())
                .description(contractProviderSubcontractorRequestDto.getDescription())
                .dateStart(contractProviderSubcontractorRequestDto.getDateStart())
                .labor(contractProviderSubcontractorRequestDto.getLabor())
                .hse(contractProviderSubcontractorRequestDto.getHse())
                .responsible(contractProviderSupplier.getResponsible())
                .expenseType(contractProviderSubcontractorRequestDto.getExpenseType())
                .contractProviderSupplier(contractProviderSupplier)
                .activities(activities)
                .providerSubcontractor(newProviderSubcontractor)
                .providerSupplier(providerSupplier)
                .build());

        documentSupplier = documentProviderSupplierRepository.findAllById(idDocuments);

        ProviderSubcontractor finalNewProviderSubcontractor = newProviderSubcontractor;
        documentSupplier.forEach(
                document -> {
                    switch (document.getDocumentMatrix().getSubGroup().getGroup().getGroupName().toLowerCase()) {
                        case "documento empresa", "documento pessoa" -> {
                            documentProviderSubcontractor.add(DocumentProviderSubcontractor.builder()
                                    .title(document.getTitle())
                                    .status(Document.Status.PENDENTE)
                                    .type(document.getType())
                                    .isActive(true)
                                    .documentMatrix(document.getDocumentMatrix())
                                    .providerSubcontractor(finalNewProviderSubcontractor)
                                    .build());
                        }
                        case "documento empresa-serviço" -> {
                            documentContract.add(DocumentContract.builder()
                                    .title(document.getTitle())
                                    .status(Document.Status.PENDENTE)
                                    .type(document.getType())
                                    .isActive(true)
                                    .documentMatrix(document.getDocumentMatrix())
                                    .contract(savedContractSubcontractor)
                                    .build());
                        }
                    }
                });

        documentProviderSubcontractorRepository.saveAll(documentProviderSubcontractor);
        documentContractRepository.saveAll(documentContract);

        // criar solicitação
        crudItemManagementImpl.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                .title(String.format("Novo fornecedor %s", newProviderSubcontractor.getCorporateName()))
                .details(String.format("Solicitação de adição do fornecedor %s - %s a plataforma",newProviderSubcontractor.getCorporateName(),newProviderSubcontractor.getCnpj()))
                .idRequester(contractProviderSubcontractorRequestDto.getIdRequester())
                .idNewProvider(newProviderSubcontractor.getIdProvider())
                .build());

        return ContractSubcontractorResponseDto.builder()
                .idContract(savedContractSubcontractor.getIdContract())
                .serviceType(savedContractSubcontractor.getServiceTypeBranch() != null ? savedContractSubcontractor.getServiceTypeBranch().getIdServiceType() : null)
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .idResponsible(savedContractSubcontractor.getResponsible() != null ? savedContractSubcontractor.getResponsible().getIdUser() : null)
                .expenseType(savedContractSubcontractor.getExpenseType())
                .dateStart(savedContractSubcontractor.getDateStart())
                .idContractSupplier(savedContractSubcontractor.getContractProviderSupplier() != null ? savedContractSubcontractor.getContractProviderSupplier().getIdContract() : null)
                .activities(savedContractSubcontractor.getActivities()
                        .stream().map(Activity::getIdActivity).toList())
                .isActive(savedContractSubcontractor.getIsActive())
                .idSubcontractor(savedContractSubcontractor.getProviderSubcontractor() != null ? savedContractSubcontractor.getProviderSubcontractor().getIdProvider() : null)
                .nameSubcontractor(savedContractSubcontractor.getProviderSubcontractor() != null ? savedContractSubcontractor.getProviderSubcontractor().getCorporateName() : null)
                .idSupplier(savedContractSubcontractor.getProviderSupplier() != null ? savedContractSubcontractor.getProviderSupplier().getIdProvider() : null)
                .nameSupplier(savedContractSubcontractor.getProviderSupplier() != null ? savedContractSubcontractor.getProviderSupplier().getCorporateName() : null)
                .build();
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> findOne(String id) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new NotFoundException("Contract not found"));

        ContractSubcontractorResponseDto contractProviderResponseDto = ContractSubcontractorResponseDto.builder()
                .idContract(contractProviderSubcontractor.getIdContract())
                .serviceType(contractProviderSubcontractor.getServiceTypeBranch() != null ? contractProviderSubcontractor.getServiceTypeBranch().getIdServiceType() : null)
                .serviceName(contractProviderSubcontractor.getServiceName())
                .contractReference(contractProviderSubcontractor.getContractReference())
                .description(contractProviderSubcontractor.getDescription())
                .idResponsible(contractProviderSubcontractor.getResponsible() != null ? contractProviderSubcontractor.getResponsible().getIdUser() : null)
                .expenseType(contractProviderSubcontractor.getExpenseType())
                .dateStart(contractProviderSubcontractor.getDateStart())
                .idContractSupplier(contractProviderSubcontractor.getContractProviderSupplier() != null ? contractProviderSubcontractor.getContractProviderSupplier().getIdContract() : null)
                .activities(contractProviderSubcontractor.getActivities()
                        .stream().map(Activity::getIdActivity).toList())
                .isActive(contractProviderSubcontractor.getIsActive())
                .idSubcontractor(contractProviderSubcontractor.getProviderSubcontractor() != null ? contractProviderSubcontractor.getProviderSubcontractor().getIdProvider() : null)
                .nameSubcontractor(contractProviderSubcontractor.getProviderSubcontractor() != null ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName() : null)
                .idSupplier(contractProviderSubcontractor.getProviderSupplier() != null ? contractProviderSubcontractor.getProviderSupplier().getIdProvider() : null)
                .nameSupplier(contractProviderSubcontractor.getProviderSupplier() != null ? contractProviderSubcontractor.getProviderSupplier().getCorporateName() : null)
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAllByIsActiveIsTrue(pageable);

        Page<ContractSubcontractorResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractSubcontractorResponseDto.builder()
                        .idContract(contractProviderSubcontractor.getIdContract())
                        .serviceType(contractProviderSubcontractor.getServiceTypeBranch() != null ? contractProviderSubcontractor.getServiceTypeBranch().getIdServiceType() : null)
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .idResponsible(contractProviderSubcontractor.getResponsible() != null ? contractProviderSubcontractor.getResponsible().getIdUser() : null)
                        .expenseType(contractProviderSubcontractor.getExpenseType())
                        .dateStart(contractProviderSubcontractor.getDateStart())
                        .idContractSupplier(contractProviderSubcontractor.getContractProviderSupplier() != null ? contractProviderSubcontractor.getContractProviderSupplier().getIdContract() : null)
                        .activities(contractProviderSubcontractor.getActivities()
                                .stream().map(Activity::getIdActivity).toList())
                        .isActive(contractProviderSubcontractor.getIsActive())
                        .idSubcontractor(contractProviderSubcontractor.getProviderSubcontractor() != null ? contractProviderSubcontractor.getProviderSubcontractor().getIdProvider() : null)
                        .nameSubcontractor(contractProviderSubcontractor.getProviderSubcontractor() != null ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName() : null)
                        .idSupplier(contractProviderSubcontractor.getProviderSupplier() != null ? contractProviderSubcontractor.getProviderSupplier().getIdProvider() : null)
                        .nameSupplier(contractProviderSubcontractor.getProviderSupplier() != null ? contractProviderSubcontractor.getProviderSupplier().getCorporateName() : null)
                        .build()
        );

        return contractProviderResponseDtoPage;
    }

    @Override
    public Optional<ContractResponseDto> update(String id, ContractRequestDto contractProviderSubcontractorRequestDto) {
        Activity activity = null;
        List<Requirement> requirements = List.of();

        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);
        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new NotFoundException("Contract not found"));

        Optional<UserProviderSupplier> providerSupplierOptional = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdProviderSupplier());
        UserProviderSupplier userProviderSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

//        if (contractProviderSubcontractorRequestDto.getIdActivity() != null && !contractProviderSubcontractorRequestDto.getIdActivity().isEmpty()) {
//            activity = activityRepository.findById(contractProviderSubcontractorRequestDto.getIdActivity()).orElseThrow(() -> new NotFoundException("Activity not found"));
//        }

//        contractProviderSubcontractor.setServiceTypeBranch(contractProviderSubcontractorRequestDto.getServiceType() != null ? contractProviderSubcontractorRequestDto.getServiceType() : contractProviderSubcontractor.getServiceTypeBranch());
        contractProviderSubcontractor.setServiceDuration(contractProviderSubcontractorRequestDto.getServiceDuration() != null ? contractProviderSubcontractorRequestDto.getServiceDuration() : contractProviderSubcontractor.getServiceDuration());
        contractProviderSubcontractor.setServiceName(contractProviderSubcontractorRequestDto.getServiceName() != null ? contractProviderSubcontractorRequestDto.getServiceName() : contractProviderSubcontractor.getServiceName());
        contractProviderSubcontractor.setContractReference(contractProviderSubcontractorRequestDto.getContractReference() != null ? contractProviderSubcontractorRequestDto.getContractReference() : contractProviderSubcontractor.getContractReference());
        contractProviderSubcontractor.setDescription(contractProviderSubcontractorRequestDto.getDescription() != null ? contractProviderSubcontractorRequestDto.getDescription() : contractProviderSubcontractor.getDescription());
        contractProviderSubcontractor.setAllocatedLimit(contractProviderSubcontractorRequestDto.getAllocatedLimit() != null ? contractProviderSubcontractorRequestDto.getAllocatedLimit() : contractProviderSubcontractor.getAllocatedLimit());
        contractProviderSubcontractor.setResponsible(contractProviderSubcontractorRequestDto.getResponsible() != null ? userProviderSupplier : contractProviderSubcontractor.getResponsible());
        contractProviderSubcontractor.setExpenseType(contractProviderSubcontractorRequestDto.getExpenseType() != null ? contractProviderSubcontractorRequestDto.getExpenseType() : contractProviderSubcontractor.getExpenseType());
        contractProviderSubcontractor.setDateStart(contractProviderSubcontractorRequestDto.getStartDate() != null ? contractProviderSubcontractorRequestDto.getStartDate() : contractProviderSubcontractor.getDateStart());
        contractProviderSubcontractor.setEndDate(contractProviderSubcontractorRequestDto.getEndDate() != null ? contractProviderSubcontractorRequestDto.getEndDate() : contractProviderSubcontractor.getEndDate());
//        contractProviderSubcontractor.setActivity(contractProviderSubcontractorRequestDto.getIdActivity() != null ? activity : contractProviderSubcontractor.getActivity());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(contractProviderSubcontractor);

        ContractResponseDto contractSubcontractorResponse = ContractResponseDto.builder()
                .idContract(savedContractSubcontractor.getIdContract())
//                .serviceType(savedContractSubcontractor.getServiceTypeBranch())
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .responsible(savedContractSubcontractor.getResponsible().getIdUser())
                .expenseType(savedContractSubcontractor.getExpenseType())
                .dateStart(savedContractSubcontractor.getDateStart())
                .endDate(savedContractSubcontractor.getEndDate())
//                .activity(savedContractSubcontractor.getActivity())
                .contractSupplierId(savedContractSubcontractor.getContractProviderSupplier().getIdContract())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractorName(savedContractSubcontractor.getProviderSubcontractor().getCorporateName())
                .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                .build();

        return Optional.of(contractSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        contractProviderSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ContractResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        Page<ContractResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractResponseDto.builder()
                        .idContract(contractProviderSubcontractor.getIdContract())
//                        .serviceType(contractProviderSubcontractor.getServiceTypeBranch())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .expenseType(contractProviderSubcontractor.getExpenseType())
                        .dateStart(contractProviderSubcontractor.getDateStart())
                        .endDate(contractProviderSubcontractor.getEndDate())
//                        .activity(contractProviderSubcontractor.getActivity())
                        .contractSupplierId(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                        .providerSubcontractorName(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                        .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                        .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                        .build()
        );

        return contractProviderResponseDtoPage;
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        Page<ContractResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractResponseDto.builder()
                        .idContract(contractProviderSubcontractor.getIdContract())
//                        .serviceType(contractProviderSubcontractor.getServiceTypeBranch())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .expenseType(contractProviderSubcontractor.getExpenseType())
                        .dateStart(contractProviderSubcontractor.getDateStart())
                        .endDate(contractProviderSubcontractor.getEndDate())
//                        .activity(contractProviderSubcontractor.getActivity())
                        .contractSupplierId(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                        .providerSubcontractorName(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                        .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                        .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                        .build()
        );

        return contractProviderResponseDtoPage;
    }
}