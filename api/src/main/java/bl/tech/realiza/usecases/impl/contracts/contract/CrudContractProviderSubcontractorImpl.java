package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
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
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
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
    private final CrudItemManagement crudItemManagementImpl;
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
                        case "documento empresa", "documento pessoa", "treinamentos e certificações" -> {
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

        return toContractSubcontractorResponseDtos(savedContractSubcontractor);
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> findOne(String id) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        return Optional.of(toContractSubcontractorResponseDtos(contractProviderSubcontractor));
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository
                .findAllByIsActiveIsTrue(pageable);

        return contractProviderSubcontractorPage.map(this::toContractSubcontractorResponseDtos);
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> update(String id, ContractRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Optional<UserProviderSupplier> providerSupplierOptional = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdProviderSupplier());
        UserProviderSupplier userProviderSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSubcontractorRequestDto.getHse() && !contractProviderSubcontractorRequestDto.getIdActivityList().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getIdActivityList());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        contractProviderSubcontractor.setServiceName(contractProviderSubcontractorRequestDto.getServiceName() != null ? contractProviderSubcontractorRequestDto.getServiceName() : contractProviderSubcontractor.getServiceName());
        contractProviderSubcontractor.setContractReference(contractProviderSubcontractorRequestDto.getContractReference() != null ? contractProviderSubcontractorRequestDto.getContractReference() : contractProviderSubcontractor.getContractReference());
        contractProviderSubcontractor.setDescription(contractProviderSubcontractorRequestDto.getDescription() != null ? contractProviderSubcontractorRequestDto.getDescription() : contractProviderSubcontractor.getDescription());
        contractProviderSubcontractor.setResponsible(contractProviderSubcontractorRequestDto.getResponsible() != null ? userProviderSupplier : contractProviderSubcontractor.getResponsible());
        contractProviderSubcontractor.setExpenseType(contractProviderSubcontractorRequestDto.getExpenseType() != null ? contractProviderSubcontractorRequestDto.getExpenseType() : contractProviderSubcontractor.getExpenseType());
        contractProviderSubcontractor.setDateStart(contractProviderSubcontractorRequestDto.getStartDate() != null ? contractProviderSubcontractorRequestDto.getStartDate() : contractProviderSubcontractor.getDateStart());
        contractProviderSubcontractor.setEndDate(contractProviderSubcontractorRequestDto.getEndDate() != null ? contractProviderSubcontractorRequestDto.getEndDate() : contractProviderSubcontractor.getEndDate());
        contractProviderSubcontractor.setActivities(contractProviderSubcontractorRequestDto.getIdActivityList() != null ? activities : contractProviderSubcontractor.getActivities());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(contractProviderSubcontractor);


        return Optional.of(toContractSubcontractorResponseDtos(savedContractSubcontractor));
    }

    @Override
    public void delete(String id) {
        contractProviderSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository
                .findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return contractProviderSubcontractorPage.map(this::toContractSubcontractorResponseDtos);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository
                .findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return contractProviderSubcontractorPage.map(this::toContractSubcontractorResponseDtos);
    }

    private ContractSubcontractorResponseDto toContractSubcontractorResponseDtos(ContractProviderSubcontractor contractProviderSubcontractor) {
            return ContractSubcontractorResponseDto.builder()
                    .idContract(contractProviderSubcontractor.getIdContract())
                    .serviceType(contractProviderSubcontractor.getServiceTypeBranch().getIdServiceType())
                    .serviceName(contractProviderSubcontractor.getServiceName())
                    .contractReference(contractProviderSubcontractor.getContractReference())
                    .description(contractProviderSubcontractor.getDescription())
                    .idResponsible(contractProviderSubcontractor.getResponsible().getIdUser())
                    .expenseType(contractProviderSubcontractor.getExpenseType())
                    .dateStart(contractProviderSubcontractor.getDateStart())
                    .finished(contractProviderSubcontractor.getFinished())
                    .isActive(contractProviderSubcontractor.getIsActive())
                    .activities(contractProviderSubcontractor.getActivities()
                            .stream().map(Activity::getIdActivity).toList())
                    .idSupplier(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                    .nameSupplier(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                    .idSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                    .nameSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                    .build();
    }
}