package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.contract.DocumentContractRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractAndSupplierCreateRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierPostRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractAndSupplierCreateResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
import bl.tech.realiza.usecases.interfaces.contracts.CrudContractProviderSupplier;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudContractProviderSupplierImpl implements CrudContractProviderSupplier {

    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ActivityRepository activityRepository;
    private final RequirementRepository requirementRepository;
    private final UserClientRepository userClientRepository;
    private final ClientRepository clientRepository;
    private final BranchRepository branchRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentContractRepository documentContractRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final CrudProviderSupplier crudProviderSupplier;
    private final CrudItemManagementImpl crudItemManagementImpl;

    @Override
    public ContractSupplierResponseDto save(ContractSupplierPostRequestDto contractProviderSupplierRequestDto) {
        List<Requirement> requirements = List.of();
        List<DocumentBranch> documentBranch = List.of();

        UserClient userClient = userClientRepository.findById(contractProviderSupplierRequestDto.getIdResponsible())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Branch branch = branchRepository.findById(contractProviderSupplierRequestDto.getIdBranch())
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Activity activity = activityRepository.findById(contractProviderSupplierRequestDto.getIdActivity())
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        if (contractProviderSupplierRequestDto.getRequirements() != null && !contractProviderSupplierRequestDto.getRequirements().isEmpty()) {
            requirements = requirementRepository.findAllById(contractProviderSupplierRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new NotFoundException("Requirements not found");
            }
        }

        switch (activity.getRisk()) {
            case LOW -> {
                documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndLowRiskIsTrue(contractProviderSupplierRequestDto.getIdBranch(), "Documentos empresa-serviço");
            }
            case MEDIUM -> {
                documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndMediumRiskIsTrue(contractProviderSupplierRequestDto.getIdBranch(), "Documentos empresa-serviço");
            }
            case HIGH -> {
                documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndHighRiskIsTrue(contractProviderSupplierRequestDto.getIdBranch(), "Documentos empresa-serviço");
            }
            default -> throw new BadRequestException("Invalid activity");
        }

        ProviderSupplier newProviderSupplier = providerSupplierRepository.save(ProviderSupplier.builder()
                .cnpj(contractProviderSupplierRequestDto.getProviderDatas().getCnpj())
                .corporateName(contractProviderSupplierRequestDto.getCorporateName())
                .email(contractProviderSupplierRequestDto.getProviderDatas().getEmail())
                .telephone(contractProviderSupplierRequestDto.getProviderDatas().getTelephone())
                .build());

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(ContractProviderSupplier.builder()
                .serviceType(contractProviderSupplierRequestDto.getServiceType())
                .serviceDuration(contractProviderSupplierRequestDto.getServiceDuration())
                .serviceName(contractProviderSupplierRequestDto.getServiceName())
                .contractReference(contractProviderSupplierRequestDto.getContractReference())
                .description(contractProviderSupplierRequestDto.getDescription())
                .allocatedLimit(contractProviderSupplierRequestDto.getAllocatedLimit())
                .dateStart(contractProviderSupplierRequestDto.getDateStart())
                .responsible(userClient)
                .expenseType(contractProviderSupplierRequestDto.getExpenseType())
                .subcontractPermission(contractProviderSupplierRequestDto.getSubcontractPermission())
                .activity(activity)
                .requirements(requirements)
                .providerSupplier(newProviderSupplier)
                .branch(branch)
                .build());

        List<DocumentMatrix> documentMatrixList = documentBranch.stream()
                .map(DocumentBranch::getDocumentMatrix)
                .toList();

        List<DocumentContract> documentProviderSuppliers = documentMatrixList.stream()
                .map(docMatrix -> DocumentContract.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .contract(savedContractProviderSupplier)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentContractRepository.saveAll(documentProviderSuppliers);

        // criar solicitação
        crudItemManagementImpl.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                        .title(String.format("Novo fornecedor %s", newProviderSupplier.getCorporateName()))
                        .details(String.format("Solicitação de adição do fornecedor %s - %s a plataforma",newProviderSupplier.getCorporateName(),newProviderSupplier.getCnpj()))
                        .idRequester(contractProviderSupplierRequestDto.getIdRequester())
                        .idNewProvider(newProviderSupplier.getIdProvider())
                .build());

        return ContractSupplierResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .idResponsible(savedContractProviderSupplier.getResponsible().getIdUser())
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .dateStart(savedContractProviderSupplier.getDateStart())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activity(savedContractProviderSupplier.getActivity())
                .requirements(savedContractProviderSupplier.getRequirements())
                .isActive(savedContractProviderSupplier.getIsActive())
                .idSupplier(savedContractProviderSupplier.getProviderSupplier().getIdProvider())
                .nameSupplier(savedContractProviderSupplier.getProviderSupplier().getCorporateName())
                .idBranch(savedContractProviderSupplier.getBranch().getIdBranch())
                .nameBranch(savedContractProviderSupplier.getBranch().getName())
                .build();
    }

    @Override
    public Optional<ContractResponseDto> findOne(String id) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(id);

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        ContractResponseDto contractResponseDto = ContractResponseDto.builder()
                .idContract(contractProviderSupplier.getIdContract())
                .serviceType(contractProviderSupplier.getServiceType())
                .serviceDuration(contractProviderSupplier.getServiceDuration())
                .serviceName(contractProviderSupplier.getServiceName())
                .contractReference(contractProviderSupplier.getContractReference())
                .description(contractProviderSupplier.getDescription())
                .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                .responsible(contractProviderSupplier.getResponsible().getIdUser())
                .expenseType(contractProviderSupplier.getExpenseType())
                .dateStart(contractProviderSupplier.getDateStart())
                .endDate(contractProviderSupplier.getEndDate())
                .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
                .activity(contractProviderSupplier.getActivity())
                .requirements(contractProviderSupplier.getRequirements())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplierName(contractProviderSupplier.getProviderSupplier().getCorporateName())
                .branch(contractProviderSupplier.getBranch().getIdBranch())
                .branchName(contractProviderSupplier.getBranch().getName())
                .build();

        return Optional.of(contractResponseDto);
    }

    @Override
    public Page<ContractResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByIsActiveIsTrue(pageable);

        return contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceType())
                        .serviceDuration(contractProviderSupplier.getServiceDuration())
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                        .responsible(contractProviderSupplier.getResponsible().getIdUser())
                        .expenseType(contractProviderSupplier.getExpenseType())
                        .dateStart(contractProviderSupplier.getDateStart())
                        .endDate(contractProviderSupplier.getEndDate())
                        .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
                        .activity(contractProviderSupplier.getActivity())
                        .requirements(contractProviderSupplier.getRequirements())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                        .providerSupplierName(contractProviderSupplier.getProviderSupplier().getCorporateName())
                        .branch(contractProviderSupplier.getBranch().getIdBranch())
                        .branchName(contractProviderSupplier.getBranch().getName())
                        .build()
        );
    }

    @Override
    public Optional<ContractResponseDto> update(String id, ContractRequestDto contractProviderSupplierRequestDto) {
        Activity activity = null;

        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(id);

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        Optional<UserClient> userClientOptional = userClientRepository.findById(contractProviderSupplierRequestDto.getResponsible());

        UserClient userClient = userClientOptional.orElseThrow(() -> new NotFoundException("User not found"));

        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();

        if (contractProviderSupplierRequestDto.getIdActivity() != null && !contractProviderSupplierRequestDto.getIdActivity().isEmpty()) {
            activity = activityRepository.findById(contractProviderSupplierRequestDto.getIdActivity()).orElseThrow(() -> new NotFoundException("Activity not found"));
        }

        if (contractProviderSupplierRequestDto.getRequirements() != null && !contractProviderSupplierRequestDto.getRequirements().isEmpty()) {
            requirements = requirementRepository.findAllById(contractProviderSupplierRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new NotFoundException("Requirements not found");
            }
        }

        contractProviderSupplier.setServiceType(contractProviderSupplierRequestDto.getServiceType() != null ? contractProviderSupplierRequestDto.getServiceType() : contractProviderSupplier.getServiceType());
        contractProviderSupplier.setServiceDuration(contractProviderSupplierRequestDto.getServiceDuration() != null ? contractProviderSupplierRequestDto.getServiceDuration() : contractProviderSupplier.getServiceDuration());
        contractProviderSupplier.setServiceName(contractProviderSupplierRequestDto.getServiceName() != null ? contractProviderSupplierRequestDto.getServiceName() : contractProviderSupplier.getServiceName());
        contractProviderSupplier.setContractReference(contractProviderSupplierRequestDto.getContractReference() != null ? contractProviderSupplierRequestDto.getContractReference() : contractProviderSupplier.getContractReference());
        contractProviderSupplier.setDescription(contractProviderSupplierRequestDto.getDescription() != null ? contractProviderSupplierRequestDto.getDescription() : contractProviderSupplier.getDescription());
        contractProviderSupplier.setAllocatedLimit(contractProviderSupplierRequestDto.getAllocatedLimit() != null ? contractProviderSupplierRequestDto.getAllocatedLimit() : contractProviderSupplier.getAllocatedLimit());
        contractProviderSupplier.setResponsible(contractProviderSupplierRequestDto.getResponsible() != null ? userClient : contractProviderSupplier.getResponsible());
        contractProviderSupplier.setExpenseType(contractProviderSupplierRequestDto.getExpenseType() != null ? contractProviderSupplierRequestDto.getExpenseType() : contractProviderSupplier.getExpenseType());
        contractProviderSupplier.setDateStart(contractProviderSupplierRequestDto.getStartDate() != null ? contractProviderSupplierRequestDto.getStartDate() : contractProviderSupplier.getDateStart());
        contractProviderSupplier.setEndDate(contractProviderSupplierRequestDto.getEndDate() != null ? contractProviderSupplierRequestDto.getEndDate() : contractProviderSupplier.getEndDate());
        contractProviderSupplier.setActivity(contractProviderSupplierRequestDto.getIdActivity() != null ? activity : contractProviderSupplier.getActivity());
        contractProviderSupplier.setRequirements(contractProviderSupplierRequestDto.getRequirements() != null ? requirements : contractProviderSupplier.getRequirements());

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(contractProviderSupplier);

        ContractResponseDto contractResponseDto = ContractResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .responsible(savedContractProviderSupplier.getResponsible().getIdUser())
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .dateStart(savedContractProviderSupplier.getDateStart())
                .endDate(savedContractProviderSupplier.getEndDate())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activity(savedContractProviderSupplier.getActivity())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplierName(contractProviderSupplier.getProviderSupplier().getCorporateName())
                .branch(contractProviderSupplier.getBranch().getIdBranch())
                .branchName(contractProviderSupplier.getBranch().getName())
                .build();

        return Optional.of(contractResponseDto);
    }

    @Override
    public void delete(String id) {
        contractProviderSupplierRepository.deleteById(id);
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @Override
    public Page<ContractResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByBranch_IdBranchAndIsActiveIsTrue(idSearch, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplierAndBranch(String idSupplier, String idBranch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByBranch_IdBranchAndProviderSupplier_IdProviderAndIsActiveIsTrue(idBranch,idSupplier, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @NotNull
    private Page<ContractResponseDto> getContractResponseDtos(Page<ContractProviderSupplier> contractProviderSupplierPage) {

        return contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceType())
                        .serviceDuration(contractProviderSupplier.getServiceDuration())
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                        .expenseType(contractProviderSupplier.getExpenseType())
                        .dateStart(contractProviderSupplier.getDateStart())
                        .endDate(contractProviderSupplier.getEndDate())
                        .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
                        .activity(contractProviderSupplier.getActivity())
                        .requirements(contractProviderSupplier.getRequirements())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                        .providerSupplierName(contractProviderSupplier.getProviderSupplier().getCorporateName())
                        .branch(contractProviderSupplier.getBranch().getIdBranch())
                        .branchName(contractProviderSupplier.getBranch().getName())
                        .build()
        );
    }

    @Override
    public ContractAndSupplierCreateResponseDto saveContractAndSupplier(ContractAndSupplierCreateRequestDto contractAndSupplierCreateRequestDto) {
        Activity activity = null;
        List<Requirement> requirements = List.of();

        if (contractAndSupplierCreateRequestDto.getBranch() == null || contractAndSupplierCreateRequestDto.getBranch().isEmpty()) {
            throw new BadRequestException("Invalid branches");
        }
        Branch branch = branchRepository.findById(contractAndSupplierCreateRequestDto.getBranch()).orElseThrow(() -> new NotFoundException("Branch not found"));
        List<Branch> branches = new ArrayList<>();
        branches.add(branch);

        List<DocumentBranch> documentBranch = documentBranchRepository.findAllByBranch_IdBranch(contractAndSupplierCreateRequestDto.getBranch());
        List<DocumentMatrix> documentMatrixList = documentBranch.stream()
                .map(DocumentBranch::getDocumentMatrix)
                .toList();

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(contractAndSupplierCreateRequestDto.getCnpj())
                .tradeName(contractAndSupplierCreateRequestDto.getTradeName())
                .corporateName(contractAndSupplierCreateRequestDto.getCorporateName())
                .email(contractAndSupplierCreateRequestDto.getEmail())
                .cep(contractAndSupplierCreateRequestDto.getCep())
                .state(contractAndSupplierCreateRequestDto.getState())
                .city(contractAndSupplierCreateRequestDto.getCity())
                .address(contractAndSupplierCreateRequestDto.getAddress())
                .number(contractAndSupplierCreateRequestDto.getNumber())
                .branches(branches)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        List<DocumentProviderSupplier> documentProviderSuppliers = documentMatrixList.stream()
                .map(docMatrix -> DocumentProviderSupplier.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .providerSupplier(savedProviderSupplier)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentProviderSupplierRepository.saveAll(documentProviderSuppliers);


        UserClient userClient = userClientRepository.findById(contractAndSupplierCreateRequestDto.getResponsible()).orElseThrow(() -> new NotFoundException("User not found"));

        activity = activityRepository.findById(contractAndSupplierCreateRequestDto.getIdActivity()).orElseThrow(() -> new NotFoundException("Activity not found"));

        if (contractAndSupplierCreateRequestDto.getRequirements() != null && !contractAndSupplierCreateRequestDto.getRequirements().isEmpty()) {
            requirements = requirementRepository.findAllById(contractAndSupplierCreateRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new NotFoundException("Requirements not found");
            }
        }

        switch (activity.getRisk()) {
            case LOW -> {
                documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndLowRiskIsTrue(contractAndSupplierCreateRequestDto.getBranch(), "Documentos empresa-serviço");
            }
            case MEDIUM -> {
                documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndMediumRiskIsTrue(contractAndSupplierCreateRequestDto.getBranch(), "Documentos empresa-serviço");
            }
            case HIGH -> {
                documentBranch = documentBranchRepository.findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndHighRiskIsTrue(contractAndSupplierCreateRequestDto.getBranch(), "Documentos empresa-serviço");
            }
            default -> throw new BadRequestException("Invalid activity");
        }

        documentMatrixList = documentBranch.stream()
                .map(DocumentBranch::getDocumentMatrix)
                .toList();

        ContractProviderSupplier newContractSupplier = ContractProviderSupplier.builder()
                .serviceType(contractAndSupplierCreateRequestDto.getServiceType())
                .serviceDuration(contractAndSupplierCreateRequestDto.getServiceDuration())
                .serviceName(contractAndSupplierCreateRequestDto.getServiceName())
                .contractReference(contractAndSupplierCreateRequestDto.getContractReference())
                .description(contractAndSupplierCreateRequestDto.getDescription())
                .allocatedLimit(contractAndSupplierCreateRequestDto.getAllocatedLimit())
                .responsible(userClient)
                .expenseType(contractAndSupplierCreateRequestDto.getExpenseType())
                .dateStart(contractAndSupplierCreateRequestDto.getStartDate())
                .endDate(contractAndSupplierCreateRequestDto.getEndDate())
                .subcontractPermission(contractAndSupplierCreateRequestDto.getSubcontractPermission())
                .activity(activity)
                .requirements(requirements)
                .providerSupplier(savedProviderSupplier)
                .branch(branch)
                .build();

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(newContractSupplier);

        List<DocumentContract> documentContractProviderSuppliers = documentMatrixList.stream()
                .map(docMatrix -> DocumentContract.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .contract(savedContractProviderSupplier)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentContractRepository.saveAll(documentContractProviderSuppliers);

        ContractAndSupplierCreateResponseDto contractAndSupplierCreateResponseDto = ContractAndSupplierCreateResponseDto.builder()
                .idProviderSupplier(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .tradeName(savedProviderSupplier.getTradeName())
                .corporateName(savedProviderSupplier.getCorporateName())
                .email(savedProviderSupplier.getEmail())
                .cep(savedProviderSupplier.getCep())
                .state(savedProviderSupplier.getState())
                .city(savedProviderSupplier.getCity())
                .address(savedProviderSupplier.getAddress())
                .number(savedProviderSupplier.getNumber())
                .branches(savedProviderSupplier.getBranches().stream().map(
                                savedBranch -> ProviderResponseDto.BranchDto.builder()
                                        .idBranch(savedBranch.getIdBranch())
                                        .nameBranch(savedBranch.getName())
                                        .build())
                        .collect(Collectors.toList()))
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .responsible(savedContractProviderSupplier.getResponsible().getIdUser())
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .startDate(savedContractProviderSupplier.getDateStart())
                .endDate(savedContractProviderSupplier.getEndDate())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activity(savedContractProviderSupplier.getActivity())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplierName(savedContractProviderSupplier.getProviderSupplier().getCorporateName())
                .idBranch(savedContractProviderSupplier.getBranch().getIdBranch())
                .branchName(savedContractProviderSupplier.getBranch().getName())
                .build();

        return contractAndSupplierCreateResponseDto;
    }
}
