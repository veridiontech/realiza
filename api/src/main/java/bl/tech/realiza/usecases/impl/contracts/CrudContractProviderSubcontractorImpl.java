package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.documents.contract.DocumentContractRepository;
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
import bl.tech.realiza.usecases.interfaces.contracts.CrudContractProviderSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public ContractSubcontractorResponseDto save(ContractSubcontractorPostRequestDto contractProviderSubcontractorRequestDto) {
        List<Requirement> requirements = List.of();
        List<DocumentProviderSupplier> documentSuplier = List.of();

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdContractSupplier())
                .orElseThrow(() -> new NotFoundException("Supplier Contract not found"));

        if (!contractProviderSupplier.getSubcontractPermission()) {
            throw new UnprocessableEntityException("Contract can't get subcontracted");
        }

        UserProviderSupplier userProviderSupplier = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdResponsible())
                .orElseThrow(() -> new NotFoundException("User supplier not found"));

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        Activity activity = activityRepository.findById(contractProviderSubcontractorRequestDto.getIdActivity())
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        if (contractProviderSubcontractorRequestDto.getRequirements() != null && !contractProviderSubcontractorRequestDto.getRequirements().isEmpty()) {
            requirements = requirementRepository.findAllById(contractProviderSubcontractorRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new NotFoundException("Requirements not found");
            }
        }

//        switch (activity.getRisk()) {
//            case LOW -> {
//                documentSuplier = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndLowRiskIsTrue(providerSupplier.getIdProvider(), "Documentos empresa-serviço");
//            }
//            case MEDIUM -> {
//                documentSuplier = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndMediumRiskIsTrue(providerSupplier.getIdProvider(), "Documentos empresa-serviço");
//            }
//            case HIGH -> {
//                documentSuplier = documentProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndHighRiskIsTrue(providerSupplier.getIdProvider(), "Documentos empresa-serviço");
//            }
//            default -> throw new BadRequestException("Invalid activity");
//        }

        ProviderSubcontractor newProviderSubcontractor = providerSubcontractorRepository.save(ProviderSubcontractor.builder()
                .cnpj(contractProviderSubcontractorRequestDto.getProviderDatas().getCnpj())
                .corporateName(contractProviderSubcontractorRequestDto.getCorporateName())
                .email(contractProviderSubcontractorRequestDto.getProviderDatas().getEmail())
                .telephone(contractProviderSubcontractorRequestDto.getProviderDatas().getTelephone())
                .build());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(ContractProviderSubcontractor.builder()
//                .serviceTypeBranch(contractProviderSubcontractorRequestDto.getServiceType())
                .serviceDuration(contractProviderSubcontractorRequestDto.getServiceDuration())
                .serviceName(contractProviderSubcontractorRequestDto.getServiceName())
                .contractReference(contractProviderSubcontractorRequestDto.getContractReference())
                .description(contractProviderSubcontractorRequestDto.getDescription())
                .allocatedLimit(contractProviderSubcontractorRequestDto.getAllocatedLimit())
                .responsible(userProviderSupplier)
                .expenseType(contractProviderSubcontractorRequestDto.getExpenseType())
                .contractProviderSupplier(contractProviderSupplier)
//                .activity(activity)
                .providerSubcontractor(newProviderSubcontractor)
                .providerSupplier(providerSupplier)
                .build());

        List<DocumentMatrix> documentMatrixList = documentSuplier.stream()
                .map(DocumentProviderSupplier::getDocumentMatrix)
                .toList();

        List<DocumentContract> documentProviderSuppliers = documentMatrixList.stream()
                .map(docMatrix -> DocumentContract.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .contract(savedContractSubcontractor)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentContractRepository.saveAll(documentProviderSuppliers);

        // criar solicitação
        crudItemManagementImpl.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                .title(String.format("Novo fornecedor %s", newProviderSubcontractor.getCorporateName()))
                .details(String.format("Solicitação de adição do fornecedor %s - %s a plataforma",newProviderSubcontractor.getCorporateName(),newProviderSubcontractor.getCnpj()))
                .idRequester(contractProviderSubcontractorRequestDto.getIdRequester())
                .idNewProvider(newProviderSubcontractor.getIdProvider())
                .build());

        return ContractSubcontractorResponseDto.builder()
                .idContract(savedContractSubcontractor.getIdContract())
//                .serviceType(savedContractSubcontractor.getServiceTypeBranch())
                .serviceDuration(savedContractSubcontractor.getServiceDuration())
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .allocatedLimit(savedContractSubcontractor.getAllocatedLimit())
                .idResponsible(savedContractSubcontractor.getResponsible().getIdUser())
                .expenseType(savedContractSubcontractor.getExpenseType())
                .idContractSupplier(savedContractSubcontractor.getContractProviderSupplier().getIdContract())
//                .activity(savedContractSubcontractor.getActivity())
                .idSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                .nameSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getCorporateName())
                .idSupplier(savedContractSubcontractor.getProviderSupplier().getIdProvider())
                .nameSupplier(savedContractSubcontractor.getProviderSupplier().getCorporateName())
                .build();
    }

    @Override
    public Optional<ContractResponseDto> findOne(String id) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new NotFoundException("Contract not found"));

        ContractResponseDto contractProviderResponseDto = ContractResponseDto.builder()
                .idContract(contractProviderSubcontractor.getIdContract())
//                .serviceType(contractProviderSubcontractor.getServiceTypeBranch())
                .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                .serviceName(contractProviderSubcontractor.getServiceName())
                .contractReference(contractProviderSubcontractor.getContractReference())
                .description(contractProviderSubcontractor.getDescription())
                .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
                .responsible(contractProviderSubcontractor.getResponsible().getIdUser())
                .expenseType(contractProviderSubcontractor.getExpenseType())
                .dateStart(contractProviderSubcontractor.getDateStart())
                .endDate(contractProviderSubcontractor.getEndDate())
//                .activity(contractProviderSubcontractor.getActivity())
                .contractSupplierId(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractorName(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public Page<ContractResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAllByIsActiveIsTrue(pageable);

        Page<ContractResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractResponseDto.builder()
                        .idContract(contractProviderSubcontractor.getIdContract())
//                        .serviceType(contractProviderSubcontractor.getServiceTypeBranch())
                        .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
                        .responsible(contractProviderSubcontractor.getResponsible().getIdUser())
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
                .serviceDuration(savedContractSubcontractor.getServiceDuration())
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .allocatedLimit(savedContractSubcontractor.getAllocatedLimit())
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
                        .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
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
                        .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
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