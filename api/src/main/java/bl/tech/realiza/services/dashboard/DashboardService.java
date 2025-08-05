package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.ContractTypeEnum;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import bl.tech.realiza.domains.services.snapshots.clients.ClientSnapshot;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.clients.ClientSnapshotRepository;
import bl.tech.realiza.gateways.requests.dashboard.DashboardFiltersRequestDto;
import bl.tech.realiza.gateways.responses.dashboard.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.documents.Document.*;
import static bl.tech.realiza.domains.documents.Document.Status.*;
import static bl.tech.realiza.domains.documents.Document.Status.PENDENTE;
import static bl.tech.realiza.domains.employees.Employee.Situation.*;
import static bl.tech.realiza.domains.enums.ContractStatusEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final BranchRepository branchRepository;
    private final DocumentRepository documentRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ClientRepository clientRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientSnapshotRepository clientSnapshotRepository;

    public DashboardHomeResponseDto getHomeInfo(String branchId) {
        branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        double conformity;
        int activeContractQuantity;
        int activeEmployeeQuantity;
        int supplierQuantity = 0;
        int allocatedEmployeeQuantity = 0;

        // conformidade
        Object[] resultEmployeeSupplierRaw = documentEmployeeRepository
                .countTotalAndPendentesByContractSupplierBranch(branchId, APROVADO);
        Object[] resultEmployeeSupplier = (Object[]) resultEmployeeSupplierRaw[0];

        Object[] resultEmployeeSubcontractorRaw = documentEmployeeRepository
                .countTotalAndPendentesByContractSubcontractorBranch(branchId, APROVADO);
        Object[] resultEmployeeSubcontractor = (Object[]) resultEmployeeSubcontractorRaw[0];

        Object[] resultSubcontractorRaw = documentProviderSubcontractorRepository
                .countTotalAndPendentesByBranch(branchId, APROVADO);
        Object[] resultSubcontractor = (Object[]) resultSubcontractorRaw[0];

        Object[] resultSupplierRaw = documentProviderSupplierRepository
                .countTotalAndPendentesByBranch(branchId, APROVADO);
        Object[] resultSupplier = (Object[]) resultSupplierRaw[0];

        long total = getSafeLong(resultEmployeeSupplier, 0)
                + getSafeLong(resultEmployeeSubcontractor, 0)
                + getSafeLong(resultSubcontractor, 0)
                + getSafeLong(resultSupplier, 0);

        long aprovados = getSafeLong(resultEmployeeSupplier, 1)
                + getSafeLong(resultEmployeeSubcontractor, 1)
                + getSafeLong(resultSubcontractor, 1)
                + getSafeLong(resultSupplier, 1);


        conformity = total > 0 ? new BigDecimal(aprovados * 100.0 / total).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;


        // contratos ativos
        activeContractQuantity = contractProviderSupplierRepository.countByBranch_IdBranchAndStatusAndFinishedIsFalse(branchId, ACTIVE).intValue();

        activeEmployeeQuantity = employeeRepository.countAllBySupplier_Branches_IdBranchAndSituation(branchId, ALOCADO).intValue()
        + employeeRepository.countAllBySupplier_Branches_IdBranchAndSituation(branchId, DESALOCADO).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(branchId, ALOCADO).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(branchId, DESALOCADO).intValue();

        // quantidade de fornecedores
        supplierQuantity = providerSupplierRepository.countByBranches_IdBranchAndIsActiveIsTrue(branchId);

        // quantidade de funcionarios alocados
        allocatedEmployeeQuantity = employeeRepository.countAllByBranch_IdBranchAndSituation(branchId, ALOCADO);

        return DashboardHomeResponseDto.builder()
                .conformity(conformity)
                .activeContractQuantity(activeContractQuantity)
                .activeEmployeeQuantity(activeEmployeeQuantity)
                .supplierQuantity(supplierQuantity)
                .allocatedEmployeeQuantity(allocatedEmployeeQuantity)
                .build();
    }

    public DashboardDetailsResponseDto getDetailsInfo(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<DashboardDetailsResponseDto.TypeStatus> documentStatus;
        List<DashboardDetailsResponseDto.Exemption> documentExemption = new ArrayList<>();
        List<DashboardDetailsResponseDto.Pending> pendingRanking = new ArrayList<>();

        List<Object[]> resultSupplier = documentProviderSupplierRepository
                .countTotalTypesByBranch(branch.getIdBranch());
        List<Object[]> resultEmployeeSupplier = documentEmployeeRepository
                .countTotalTypesByBranch(branch.getIdBranch());
        List<Object[]> resultSubcontractor = documentProviderSupplierRepository
                .countTotalTypesByBranch(branch.getIdBranch());
        List<Object[]> resultEmployeeSubcontractor = documentEmployeeRepository
                .countTotalTypesByBranch(branch.getIdBranch());

        List<Object[]> allResults = new ArrayList<>();
        allResults.addAll(resultSupplier);
        allResults.addAll(resultEmployeeSupplier);
        allResults.addAll(resultSubcontractor);
        allResults.addAll(resultEmployeeSubcontractor);

        Map<String, Map<String, Integer>> grouped = new HashMap<>();

        for (Object[] row : allResults) {
            String docType = (String) row[0];
            Status status = (Status) row[1];
            Integer quantity = ((Number) row[2]).intValue();

            grouped
                    .computeIfAbsent(docType, k -> new HashMap<>())
                    .merge(status.name(), quantity, Integer::sum);
        }

        documentStatus = grouped.entrySet().stream()
                .map(entry -> DashboardDetailsResponseDto.TypeStatus.builder()
                        .name(entry.getKey())
                        .status(
                                entry.getValue().entrySet().stream()
                                        .map(statusEntry -> DashboardDetailsResponseDto.Status.builder()
                                                .type(statusEntry.getKey())
                                                .quantity(statusEntry.getValue())
                                                .build())
                                        .toList()
                        )
                        .build())
                .toList();

        documentExemption = documentStatus.stream()
                .map(typeStatus -> DashboardDetailsResponseDto.Exemption.builder()
                        .name(typeStatus.getName())
                        .quantity(0)
                        .build())
                .toList();

        List<Branch> allBranches = branch.getClient().getBranches();

        for (Branch b : allBranches) {
            Object[] supplierRaw = documentProviderSupplierRepository
                    .countTotalAndPendentesByBranch(b.getIdBranch(), PENDENTE);
            Object[] supplier = (Object[]) supplierRaw[0];

            Object[] employeeSupplierRaw = documentEmployeeRepository
                    .countTotalAndPendentesByContractSupplierBranch(b.getIdBranch(), PENDENTE);
            Object[] employeeSupplier = (Object[]) employeeSupplierRaw[0];

            Object[] subcontractorRaw = documentProviderSubcontractorRepository
                    .countTotalAndPendentesByBranch(b.getIdBranch(), PENDENTE);
            Object[] subcontractor = (Object[]) subcontractorRaw[0];

            Object[] employeeSubcontractorRaw = documentEmployeeRepository
                    .countTotalAndPendentesByContractSubcontractorBranch(b.getIdBranch(), PENDENTE);
            Object[] employeeSubcontractor = (Object[]) employeeSubcontractorRaw[0];


            long total = getSafeLong(supplier, 0)
                    + getSafeLong(employeeSupplier, 0)
                    + getSafeLong(subcontractor, 0)
                    + getSafeLong(employeeSubcontractor, 0);

            long pendentes = getSafeLong(supplier, 1)
                    + getSafeLong(employeeSupplier, 1)
                    + getSafeLong(subcontractor, 1)
                    + getSafeLong(employeeSubcontractor, 1);

            double adherence = total > 0 ? ((total - pendentes) * 100.0 / total) : 100;
            adherence = Math.round(adherence * 100.0) / 100.0;

            supplierRaw = documentProviderSupplierRepository
                    .countTotalAndPendentesByBranch(b.getIdBranch(), APROVADO);
            supplier = (Object[]) supplierRaw[0];

            employeeSupplierRaw = documentEmployeeRepository
                    .countTotalAndPendentesByContractSupplierBranch(b.getIdBranch(), APROVADO);
            employeeSupplier = (Object[]) employeeSupplierRaw[0];

            subcontractorRaw = documentProviderSubcontractorRepository
                    .countTotalAndPendentesByBranch(b.getIdBranch(), APROVADO);
            subcontractor = (Object[]) subcontractorRaw[0];

            employeeSubcontractorRaw = documentEmployeeRepository
                    .countTotalAndPendentesByContractSubcontractorBranch(b.getIdBranch(), APROVADO);
            employeeSubcontractor = (Object[]) employeeSubcontractorRaw[0];

            long aprovados = getSafeLong(supplier, 1)
                    + getSafeLong(employeeSupplier, 1)
                    + getSafeLong(subcontractor, 1)
                    + getSafeLong(employeeSubcontractor, 1);

            double conformity = total > 0 ? new BigDecimal(aprovados * 100.0 / total).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
            int nonConforming = (int) (total - aprovados);

            DashboardDetailsResponseDto.Conformity level;
            if (conformity < 60) {
                level = DashboardDetailsResponseDto.Conformity.RISKY;
            } else if (conformity < 75) {
                level = DashboardDetailsResponseDto.Conformity.ATTENTION;
            } else if (conformity < 90) {
                level = DashboardDetailsResponseDto.Conformity.NORMAL;
            } else {
                level = DashboardDetailsResponseDto.Conformity.OK;
            }

            pendingRanking.add(DashboardDetailsResponseDto.Pending.builder()
                    .corporateName(b.getName())
                    .cnpj(b.getCnpj())
                    .adherence(adherence)
                    .conformity(conformity)
                    .nonConformingDocumentQuantity(nonConforming)
                    .conformityLevel(level)
                    .build());
        }

        return DashboardDetailsResponseDto.builder()
                .documentStatus(documentStatus)
                .documentExemption(documentExemption)
                .pendingRanking(pendingRanking)
                .build();
    }

    public DashboardGeneralDetailsResponseDto getGeneralDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        List<String> branchIds = null;
        List<String> providerIds = null;
        List<String> documentTypes = null;
        List<String> responsibleIds = null;
        List<ContractStatusEnum> activeContract = null;
        List<Status> statuses = null;
        List<String> documentTitles = null;
        if (dashboardFiltersRequestDto != null) {
            branchIds = dashboardFiltersRequestDto.getBranchIds() != null
                    ? dashboardFiltersRequestDto.getBranchIds()
                    : new ArrayList<>();
            providerIds = dashboardFiltersRequestDto.getProviderIds() != null
                    ? dashboardFiltersRequestDto.getProviderIds()
                    : new ArrayList<>();
            documentTypes = dashboardFiltersRequestDto.getDocumentTypes() != null
                    ? dashboardFiltersRequestDto.getDocumentTypes()
                    : new ArrayList<>();
            responsibleIds = dashboardFiltersRequestDto.getResponsibleIds() != null
                    ? dashboardFiltersRequestDto.getResponsibleIds()
                    : new ArrayList<>();
            activeContract = dashboardFiltersRequestDto.getActiveContract() != null
                    ? dashboardFiltersRequestDto.getActiveContract()
                    : new ArrayList<>();
            statuses = dashboardFiltersRequestDto.getStatuses() != null
                    ? dashboardFiltersRequestDto.getStatuses()
                    : new ArrayList<>();
            documentTitles = dashboardFiltersRequestDto.getDocumentTitles() != null
                    ? dashboardFiltersRequestDto.getDocumentTitles()
                    : new ArrayList<>();
        }
        if (branchIds != null) {
            if (branchIds.isEmpty()) {
                branchIds = null;
            }
        }
        if (providerIds != null) {
            if (providerIds.isEmpty()) {
                providerIds = null;
            }
        }
        if (documentTypes != null) {
            if (documentTypes.isEmpty()) {
                documentTypes = null;
            }
        }
        if (responsibleIds != null) {
            if (responsibleIds.isEmpty()) {
                responsibleIds = null;
            }
        }
        if (activeContract != null) {
            if (activeContract.isEmpty()) {
                activeContract = new ArrayList<>();
                activeContract.add(ACTIVE);
            }
        }
        if (statuses != null) {
            if (statuses.isEmpty()) {
                statuses = null;
            }
        }
        if (documentTitles != null) {
            if (documentTitles.isEmpty()) {
                documentTitles = null;
            }
        }
        // quantidade de fornecedores
        Long supplierQuantity = providerSupplierRepository.countByClientIdAndIsActive(clientId);

        // quantidade de contratos
        Long contractQuantity = contractProviderSupplierRepository.countByClientIdAndIsActive(clientId, activeContract);

        // funcionários alocados
        Long allocatedEmployeeQuantity = employeeRepository.countEmployeeSupplierByClientIdAndAllocated(clientId, ALOCADO)
                + employeeRepository.countEmployeeSubcontractorByClientIdAndAllocated(clientId, ALOCADO);

        // conformidade
        Double conformity = null;
        Object[] conformityValuesSupplier = null;
        Object[] conformityValuesSubcontractor = null;
        if (branchIds == null) {
            conformityValuesSupplier = documentRepository.countTotalAndConformitySupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(clientId,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles);
            conformityValuesSubcontractor = documentRepository.countTotalAndConformitySubcontractorByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(clientId,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles);
        } else {
            conformityValuesSupplier = documentRepository.countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(branchIds,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles);
            conformityValuesSubcontractor = documentRepository.countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(branchIds,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles);
        }
        Long totalConformity = getSafeLong(conformityValuesSupplier, 0) + getSafeLong(conformityValuesSubcontractor, 0);
        Long conformityTrue = getSafeLong(conformityValuesSupplier, 1) + getSafeLong(conformityValuesSubcontractor, 1);

        conformity = totalConformity > 0
                ? new BigDecimal(conformityTrue * 100.0 / totalConformity).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

        // para cada type selecionado, quantidade de documentos com status
        List<DashboardGeneralDetailsResponseDto.TypeStatus> documentStatus = new ArrayList<>();
        List<DashboardGeneralDetailsResponseDto.Exemption> documentExemption = new ArrayList<>();

        if (documentTypes == null) {
            documentTypes = documentRepository.findDistinctDocumentType();
        }
        for (String type : documentTypes) {
            List<DashboardGeneralDetailsResponseDto.Status> statusList = new ArrayList<>();
            if (statuses == null) {
                statuses = Arrays.asList(Status.values());
            }
            for (Status status : statuses) {
                int supplier = 0;
                int subcontract = 0;
                if (branchIds != null) {
                    supplier = documentRepository.countSupplierByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(branchIds,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles).intValue();
                    subcontract = documentRepository.countSubcontractorByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(branchIds,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles).intValue();
                } else {
                    supplier = documentRepository.countSupplierByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(clientId,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles).intValue();
                    subcontract = documentRepository.countSubcontractorByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(clientId,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles).intValue();
                }
                statusList.add(DashboardGeneralDetailsResponseDto.Status.builder()
                        .quantity(supplier + subcontract)
                        .status(status)
                        .build());

            }
            long approvedIa = 0;
            long reprovedIa = 0;
            DashboardGeneralDetailsResponseDto.Status statusApprovedIA = statusList.stream()
                    .filter(status -> status.getStatus() == APROVADO_IA)
                    .findFirst()
                    .orElse(null);
            DashboardGeneralDetailsResponseDto.Status statusReprovedIA = statusList.stream()
                    .filter(status -> status.getStatus() == REPROVADO_IA)
                    .findFirst()
                    .orElse(null);
            DashboardGeneralDetailsResponseDto.Status statusUnderAnalysis = statusList.stream()
                    .filter(status -> status.getStatus() == EM_ANALISE)
                    .findFirst()
                    .orElse(null);
            if (statusApprovedIA != null) {
                approvedIa = statusApprovedIA.getQuantity().longValue();
            }
            if (statusReprovedIA != null) {
                reprovedIa = statusReprovedIA.getQuantity().longValue();
            }
            if (statusUnderAnalysis != null) {
                long newQuantity = statusUnderAnalysis.getQuantity().longValue() + approvedIa + reprovedIa;
                statusUnderAnalysis.setQuantity(Math.toIntExact(newQuantity));
            }
            statusList.removeIf(status -> status.getStatus() == APROVADO_IA || status.getStatus() == REPROVADO_IA);

            documentStatus.add(DashboardGeneralDetailsResponseDto.TypeStatus.builder()
                    .name(type)
                    .status(statusList)
                    .build());
        }

        // ranking de pendencias
        List<DashboardGeneralDetailsResponseDto.Pending> pendingRanking = new ArrayList<>();
        List<String> allBranches = branchRepository.findAllBranchIdsByClientId(clientId);

        for (String branchId : allBranches) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new NotFoundException("Branch not found"));
            Double adherenceBranch = null;
            Double conformityBranch = null;
            List<String> newBranchIds = new ArrayList<>();
            newBranchIds.add(branchId);

            Object[] adherenceBranchSupplierValuesRaw = documentRepository.countTotalAndAdherenceSupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles);
            Object[] adherenceBranchSubcontractorValuesRaw = documentRepository.countTotalAndAdherenceSubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles);
            Object[] conformityBranchSupplierValuesRaw = documentRepository.countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles);
            Object[] conformityBranchSubcontractorValuesRaw = documentRepository.countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles);

            Object[] adherenceBranchSupplierValues = (Object[]) adherenceBranchSupplierValuesRaw[0];
            Object[] adherenceBranchSubcontractorValues = (Object[]) adherenceBranchSubcontractorValuesRaw[0];
            Object[] conformityBranchSupplierValues = (Object[]) conformityBranchSupplierValuesRaw[0];
            Object[] conformityBranchSubcontractorValues = (Object[]) conformityBranchSubcontractorValuesRaw[0];

            Long totalAdherenceBranch = getSafeLong(adherenceBranchSupplierValues, 0) + getSafeLong(adherenceBranchSubcontractorValues, 0);
            Long adherenceBranchTrue = getSafeLong(adherenceBranchSupplierValues, 1) + getSafeLong(adherenceBranchSubcontractorValues, 1);
            Long totalConformityBranch = getSafeLong(conformityBranchSupplierValues, 0) + getSafeLong(conformityBranchSubcontractorValues, 0);
            Long conformityBranchTrue = getSafeLong(conformityBranchSupplierValues, 1) + getSafeLong(conformityBranchSubcontractorValues, 1);
            Long nonConformityBranchTrue = (totalConformityBranch - conformityBranchTrue);

            adherenceBranch = totalAdherenceBranch > 0
                    ? new BigDecimal(adherenceBranchTrue * 100.0 / totalAdherenceBranch).setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 100;

            conformityBranch = totalConformityBranch > 0
                    ? new BigDecimal(conformityBranchTrue * 100.0 / totalConformityBranch).setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 100;

            DashboardGeneralDetailsResponseDto.Conformity level;
            if (conformityBranch < 60) {
                level = DashboardGeneralDetailsResponseDto.Conformity.RISKY;
            } else if (conformityBranch < 75) {
                level = DashboardGeneralDetailsResponseDto.Conformity.ATTENTION;
            } else if (conformityBranch < 90) {
                level = DashboardGeneralDetailsResponseDto.Conformity.NORMAL;
            } else {
                level = DashboardGeneralDetailsResponseDto.Conformity.OK;
            }

            pendingRanking.add(DashboardGeneralDetailsResponseDto.Pending.builder()
                    .corporateName(branch.getName())
                    .cnpj(branch.getCnpj())
                    .adherence(adherenceBranch)
                    .conformity(conformityBranch)
                    .nonConformingDocumentQuantity(nonConformityBranchTrue.intValue())
                    .conformityLevel(level)
                    .build());
        }

        return DashboardGeneralDetailsResponseDto.builder()
                .supplierQuantity(supplierQuantity)
                .contractQuantity(contractQuantity)
                .allocatedEmployeeQuantity(allocatedEmployeeQuantity)
                .conformity(conformity)
                .documentStatus(documentStatus)
                .documentExemption(documentExemption)
                .pendingRanking(pendingRanking)
                .build();
    }

    public List<DashboardProviderDetailsResponseDto> getProviderDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        List<String> branchIds = dashboardFiltersRequestDto != null
                ? (dashboardFiltersRequestDto.getBranchIds() != null
                    ? dashboardFiltersRequestDto.getBranchIds()
                    : new ArrayList<>() )
                : new ArrayList<>();
        List<String> documentTypes = dashboardFiltersRequestDto != null
                ? (dashboardFiltersRequestDto.getDocumentTypes() != null
                    ? dashboardFiltersRequestDto.getDocumentTypes()
                    : new ArrayList<>() )
                : new ArrayList<>();
        List<String> responsibleIds = dashboardFiltersRequestDto != null
                ? (dashboardFiltersRequestDto.getResponsibleIds() != null
                    ? dashboardFiltersRequestDto.getResponsibleIds()
                    : new ArrayList<>() )
                : new ArrayList<>();
        List<String> documentTitles = dashboardFiltersRequestDto != null
                ? (dashboardFiltersRequestDto.getDocumentTitles() != null
                    ? dashboardFiltersRequestDto.getDocumentTitles()
                    : new ArrayList<>() )
                : new ArrayList<>();

        List<DashboardProviderDetailsResponseDto> responseDtos = new ArrayList<>();
        List<ProviderSupplier> providerSuppliers = new ArrayList<>();
        List<ProviderSubcontractor> providerSubcontractors = new ArrayList<>();
        Double adherenceProvider = null;
        Double conformityProvider = null;
        Object[] adherenceProviderValues = null;
        Object[] conformityProviderValues = null;
        if (responsibleIds.isEmpty()) {
            responsibleIds = null;
        }
        if (documentTypes.isEmpty()) {
            documentTypes = null;
        }
        if (documentTitles.isEmpty()) {
            documentTitles = null;
        }
        if (branchIds.isEmpty()) {
            providerSuppliers = providerSupplierRepository.findAllByClientIdAndContractStatusAndIsActiveIsTrue(clientId, ACTIVE);
            providerSubcontractors = providerSubcontractorRepository.findAllByContractSupplierClientIdAndContractStatusAndIsActiveIsTrue(clientId, ACTIVE);
        } else {
            providerSuppliers = providerSupplierRepository.findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrue(branchIds,responsibleIds, ACTIVE);
            providerSubcontractors = providerSubcontractorRepository.findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrue(branchIds,responsibleIds, ACTIVE);
        }
        for (ProviderSupplier providerSupplier : providerSuppliers ) {
            adherenceProviderValues = documentRepository.countTotalAndAdherenceByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSupplier.getIdProvider(),
                    responsibleIds,
                    documentTypes,
                    documentTitles);
            conformityProviderValues = documentRepository.countTotalAndConformityByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSupplier.getIdProvider(),
                    responsibleIds,
                    documentTypes,
                    documentTitles);

            Long totalAdherenceProvider = getSafeLong(adherenceProviderValues,0);
            Long adherenceProviderTrue = getSafeLong(adherenceProviderValues,1);
            Long nonAdherenceProviderTrue = (totalAdherenceProvider - adherenceProviderTrue);
            Long totalConformityProvider = getSafeLong(conformityProviderValues,0);
            Long conformityProviderTrue = getSafeLong(conformityProviderValues,1);
            Long nonConformityProviderTrue = (totalConformityProvider - conformityProviderTrue);
            if (totalAdherenceProvider.equals(totalConformityProvider)) {
                log.info("Values not match in provider supplier id {}",providerSupplier.getIdProvider());
            }

            adherenceProvider = totalAdherenceProvider > 0
                    ? new BigDecimal(adherenceProviderTrue * 100.0 / totalAdherenceProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            conformityProvider = totalConformityProvider > 0
                    ? new BigDecimal(conformityProviderTrue * 100.0 / totalConformityProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            DashboardProviderDetailsResponseDto.Conformity conformityRange;
            if (conformityProvider < 60) {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.RISKY;
            } else if (conformityProvider < 75) {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.ATTENTION;
            } else if (conformityProvider < 90) {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.NORMAL;
            } else {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.OK;
            }

            responseDtos.add(
                    DashboardProviderDetailsResponseDto.builder()
                            .corporateName(providerSupplier.getCorporateName())
                            .cnpj(providerSupplier.getCnpj())
                            .totalDocumentQuantity(totalAdherenceProvider)
                            .adherenceQuantity(adherenceProviderTrue)
                            .nonAdherenceQuantity(nonAdherenceProviderTrue)
                            .conformityQuantity(conformityProviderTrue)
                            .nonConformityQuantity(nonConformityProviderTrue)
                            .adherence(adherenceProvider)
                            .conformity(conformityProvider)
                            .conformityRange(conformityRange)
                            .build()
            );
        }
        for (ProviderSubcontractor providerSubcontractor : providerSubcontractors ) {
            adherenceProviderValues = documentRepository.countTotalAndAdherenceByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSubcontractor.getIdProvider(),
                    responsibleIds,
                    documentTypes,
                    documentTitles);
            conformityProviderValues = documentRepository.countTotalAndConformityByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSubcontractor.getIdProvider(),
                    responsibleIds,
                    documentTypes,
                    documentTitles);

            Long totalAdherenceProvider = (Long) adherenceProviderValues[0];
            Long adherenceProviderTrue = (Long) adherenceProviderValues[1];
            Long nonAdherenceProviderTrue = (totalAdherenceProvider - adherenceProviderTrue);
            Long totalConformityProvider = (Long) conformityProviderValues[0];
            Long conformityProviderTrue = (Long) conformityProviderValues[1];
            Long nonConformityProviderTrue = (totalConformityProvider - conformityProviderTrue);
            if (totalAdherenceProvider.equals(totalConformityProvider)) {
                log.info("Values not match in provider subcontractor id {}",providerSubcontractor.getIdProvider());
            }

            adherenceProvider = totalAdherenceProvider > 0
                    ? new BigDecimal(adherenceProviderTrue * 100.0 / totalAdherenceProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            conformityProvider = totalConformityProvider > 0
                    ? new BigDecimal(conformityProviderTrue * 100.0 / totalConformityProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            DashboardProviderDetailsResponseDto.Conformity conformityRange;
            if (conformityProvider < 60) {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.RISKY;
            } else if (conformityProvider < 75) {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.ATTENTION;
            } else if (conformityProvider < 90) {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.NORMAL;
            } else {
                conformityRange = DashboardProviderDetailsResponseDto.Conformity.OK;
            }

            responseDtos.add(
                    DashboardProviderDetailsResponseDto.builder()
                            .corporateName(providerSubcontractor.getCorporateName())
                            .cnpj(providerSubcontractor.getCnpj())
                            .totalDocumentQuantity(totalAdherenceProvider)
                            .adherenceQuantity(adherenceProviderTrue)
                            .nonAdherenceQuantity(nonAdherenceProviderTrue)
                            .conformityQuantity(conformityProviderTrue)
                            .nonConformityQuantity(nonConformityProviderTrue)
                            .adherence(adherenceProvider)
                            .conformity(conformityProvider)
                            .conformityRange(conformityRange)
                            .build()
            );
        }
        return responseDtos;
    }

    private long getSafeLong(Object[] array, int index) {
        if (array != null && array.length > index && array[index] instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }

    public DashboardDocumentStatusResponseDto getDocumentStatusInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        DashboardDocumentStatusResponseDto responseDto = DashboardDocumentStatusResponseDto.builder().build();
        List<String> branchIds = null;
        List<String> providerIds = null;
        List<String> documentTypes = null;
        List<String> responsibleIds = null;
        List<ContractStatusEnum> activeContract = null;
        List<Status> statuses = null;
        List<String> documentTitles = null;
        if (dashboardFiltersRequestDto != null) {
            branchIds = dashboardFiltersRequestDto.getBranchIds() != null
                    ? dashboardFiltersRequestDto.getBranchIds()
                    : null;
            providerIds = dashboardFiltersRequestDto.getProviderIds() != null
                    ? dashboardFiltersRequestDto.getProviderIds()
                    : null;
            documentTypes = dashboardFiltersRequestDto.getDocumentTypes() != null
                    ? dashboardFiltersRequestDto.getDocumentTypes()
                    : null;
            responsibleIds = dashboardFiltersRequestDto.getResponsibleIds() != null
                    ? dashboardFiltersRequestDto.getResponsibleIds()
                    : null;
            activeContract = dashboardFiltersRequestDto.getActiveContract() != null
                    ? dashboardFiltersRequestDto.getActiveContract()
                    : null;
            statuses = dashboardFiltersRequestDto.getStatuses() != null
                    ? dashboardFiltersRequestDto.getStatuses()
                    : null;
            documentTitles = dashboardFiltersRequestDto.getDocumentTitles() != null
                    ? dashboardFiltersRequestDto.getDocumentTitles()
                    : null;
        }
        List<Document> documentsSupplier = new ArrayList<>();
        List<Document> documentsSubcontractor = new ArrayList<>();
        // find all documents by filters
        if (branchIds != null && !branchIds.isEmpty()) {
            documentsSupplier = documentRepository.findAllSupplierByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles);

            documentsSubcontractor = documentRepository.findAllSubcontractorByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles);
        } else {
            documentsSupplier = documentRepository.findAllSupplierByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles);

            documentsSubcontractor = documentRepository.findAllSubcontractorByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles);
        }

        // find all adherent documents by filters
        long total = documentsSupplier.size() + documentsSubcontractor.size();
        long adherentSupplier = documentsSupplier.stream()
                .filter(Document::getAdherent)
                .toList()
                .size();
        long adherentSubcontractor = documentsSubcontractor.stream()
                .filter(Document::getAdherent)
                .toList()
                .size();
        responseDto.setAdherentDocumentsQuantity(adherentSupplier + adherentSubcontractor);
        responseDto.setNonAdherentDocumentsQuantity(total - responseDto.getAdherentDocumentsQuantity());

        // find all conforming documents by filters
        long conformingSupplier = documentsSupplier.stream()
                .filter(Document::getConforming)
                .toList()
                .size();
        long conformingSubcontractor = documentsSubcontractor.stream()
                .filter(Document::getConforming)
                .toList()
                .size();
        responseDto.setConformingDocumentsQuantity(conformingSupplier + conformingSubcontractor);
        responseDto.setNonConformingDocumentsQuantity(total - responseDto.getConformingDocumentsQuantity());

        // list infos by status
        responseDto.setDocumentStatus(new ArrayList<>());
        for (Document.Status status : Document.Status.values()) {
            List<Document> documentSupplierStatus = documentsSupplier.stream()
                    .filter(document -> document.getStatus().equals(status))
                    .toList();
            List<Document> documentSubcontractorStatus = documentsSubcontractor.stream()
                    .filter(document -> document.getStatus().equals(status))
                    .toList();
            long totalStatus = documentSupplierStatus.size() + documentSubcontractorStatus.size();

            Double percentage = total > 0
                    ? new BigDecimal(totalStatus * 100.0 / total).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            DashboardDocumentStatusResponseDto.Status statusResponse = DashboardDocumentStatusResponseDto.Status.builder()
                    .status(status)
                    .adherent(status != PENDENTE && status != VENCIDO)
                    .conforming(status == APROVADO)
                    .quantity(totalStatus)
                    .percentage(percentage)
                    .build();
            responseDto.getDocumentStatus().add(statusResponse);
        }
        // show all adherent and non-adherent

        // show all conforming and non-conforming
        // find all status and infos by filters
        return responseDto;
    }

    public Page<DashboardDocumentDetailsResponseDto> getDocumentDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto, Pageable pageable) {
        DashboardDocumentDetailsResponseDto responseDto = DashboardDocumentDetailsResponseDto.builder().build();
        List<String> branchIds = null;
        List<String> providerIds = null;
        List<String> documentTypes = null;
        List<String> responsibleIds = null;
        List<ContractStatusEnum> activeContract = null;
        List<Status> statuses = null;
        List<String> documentTitles = null;
        if (dashboardFiltersRequestDto != null) {
            branchIds = dashboardFiltersRequestDto.getBranchIds() != null
                    ? dashboardFiltersRequestDto.getBranchIds()
                    : null;
            providerIds = dashboardFiltersRequestDto.getProviderIds() != null
                    ? dashboardFiltersRequestDto.getProviderIds()
                    : null;
            documentTypes = dashboardFiltersRequestDto.getDocumentTypes() != null
                    ? dashboardFiltersRequestDto.getDocumentTypes()
                    : null;
            responsibleIds = dashboardFiltersRequestDto.getResponsibleIds() != null
                    ? dashboardFiltersRequestDto.getResponsibleIds()
                    : null;
            activeContract = dashboardFiltersRequestDto.getActiveContract() != null
                    ? dashboardFiltersRequestDto.getActiveContract()
                    : null;
            statuses = dashboardFiltersRequestDto.getStatuses() != null
                    ? dashboardFiltersRequestDto.getStatuses()
                    : null;
            documentTitles = dashboardFiltersRequestDto.getDocumentTitles() != null
                    ? dashboardFiltersRequestDto.getDocumentTitles()
                    : null;
        }
        Page<Document> documentsSupplier = null;
        Page<Document> documentsSubcontractor = null;
        // find all documents by filters
        if (branchIds != null && !branchIds.isEmpty()) {
            documentsSupplier = documentRepository.findAllSupplierByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    pageable);

            documentsSubcontractor = documentRepository.findAllSubcontractorByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    pageable);
        } else {
            documentsSupplier = documentRepository.findAllSupplierByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    pageable);

            documentsSubcontractor = documentRepository.findAllSubcontractorByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    pageable);
        }
        List<Document> combinedDocuments = new ArrayList<>();
        combinedDocuments.addAll(documentsSupplier.getContent());
        combinedDocuments.addAll(documentsSubcontractor.getContent());
        combinedDocuments.sort(Comparator.comparing(Document::getTitle));
        List<Document> limitedDocuments = combinedDocuments.stream()
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        Page<Document> paginatedDocuments = new PageImpl<>(limitedDocuments, pageable, combinedDocuments.size());

        return toDetailsPageDto(paginatedDocuments);
    }

    private DashboardDocumentDetailsResponseDto toDetailsDto(Document document) {
        String branchName = null;
        String branchCnpj = null;
        String supplierName = null;
        String supplierCnpj = null;
        ContractTypeEnum contractType = null;
        String subcontractorName = null;
        String subcontractorCnpj = null;
        String employeeFullName = null;
        String employeePosition = null;
        String employeeCbo = null;

        Date contractStart = null;
        Date contractFinish = null;
        String serviceTypeName = null;
        String responsibleFullName = null;
        String responsibleEmail = null;
        ContractStatusEnum contractStatus = null;

        ContractDocument lastContractDocument = document.getContractDocuments().stream()
                .max(Comparator.comparing(ContractDocument::getCreatedAt))
                .orElse(null);
        if (lastContractDocument != null) {
            Contract contract = lastContractDocument.getContract();

            contractStart = contract.getDateStart();
            contractFinish = contract.getEndDate();
            contractStatus = contract.getStatus();
            serviceTypeName = contract.getServiceTypeBranch().getTitle();
            responsibleFullName = contract.getResponsible().getFullName();
            responsibleEmail = contract.getResponsible().getEmail();

            if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
                branchName = contractProviderSupplier.getBranch().getName();
                branchCnpj = contractProviderSupplier.getBranch().getCnpj();
                supplierName = contractProviderSupplier.getProviderSupplier().getCorporateName();
                supplierCnpj = contractProviderSupplier.getProviderSupplier().getCnpj();
                contractType = ContractTypeEnum.CONTRACT;
            } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
                branchName = contractProviderSubcontractor.getContractProviderSupplier().getBranch().getName();
                branchCnpj = contractProviderSubcontractor.getContractProviderSupplier().getBranch().getCnpj();
                supplierName = contractProviderSubcontractor.getContractProviderSupplier().getProviderSupplier().getCorporateName();
                supplierCnpj = contractProviderSubcontractor.getContractProviderSupplier().getProviderSupplier().getCnpj();
                subcontractorName = contractProviderSubcontractor.getProviderSubcontractor().getCorporateName();
                subcontractorCnpj = contractProviderSubcontractor.getProviderSubcontractor().getCnpj();
                contractType = ContractTypeEnum.SUBCONTRACT;

            }
        }

        if (document instanceof DocumentEmployee documentEmployee) {
            employeeFullName = documentEmployee.getEmployee().getFullName();
            employeePosition = documentEmployee.getEmployee().getPosition().getTitle();
            employeeCbo = documentEmployee.getEmployee().getCbo().getTitle();
        }


        return DashboardDocumentDetailsResponseDto.builder()
                .branchName(branchName)
                .branchCnpj(branchCnpj)
                .supplierName(supplierName)
                .supplierCnpj(supplierCnpj)
                .contractType(contractType)
                .subcontractorName(subcontractorName)
                .subcontractorCnpj(subcontractorCnpj)
                .employeeFullName(employeeFullName)
                .employeePosition(employeePosition)
                .employeeCbo(employeeCbo)
                .contractStart(contractStart)
                .contractFinish(contractFinish)
                .serviceTypeName(serviceTypeName)
                .responsibleFullName(responsibleFullName)
                .responsibleEmail(responsibleEmail)
                .contractStatus(contractStatus)
                .documentTitle(document.getTitle())
                .documentSubgroupName(document.getDocumentMatrix() != null
                        ? (document.getDocumentMatrix().getSubGroup() != null
                            ? document.getDocumentMatrix().getSubGroup().getSubgroupName()
                            : null)
                        : null)
                .documentGroupName(document.getDocumentMatrix() != null
                        ? (document.getDocumentMatrix().getSubGroup() != null
                            ? document.getDocumentMatrix().getSubGroup().getGroup() != null
                                ? document.getDocumentMatrix().getSubGroup().getGroup().getGroupName()
                                : null
                            : null)
                        : null)
                .documentType(document.getType())
                .doesBlock(document.getDoesBlock())
                .adherent(document.getAdherent())
                .conforming(document.getConforming())
                .status(document.getStatus())
                .versionDate(document.getVersionDate())
                .lastCheck(document.getLastCheck())
                .expirationDate(document.getExpirationDate())
                .build();
    }

    private Page<DashboardDocumentDetailsResponseDto> toDetailsPageDto(Page<Document> documents) {
        return documents.map(this::toDetailsDto);
    }

    public void takeSnapshot(SnapshotFrequencyEnum frequency) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Client> clients = clientRepository.findAllByIsActiveIsTrue(pageable);
        while (clients.hasContent()) {
            List<ClientSnapshot> clientBatch = new ArrayList<>(50);
            for (Client client : clients) {
                clientBatch.add(ClientSnapshot.builder()
                                .cnpj(client.getCnpj())
                        .build());

                if (clientBatch.size() >= 50) {
                    clientSnapshotRepository.saveAll(clientBatch);
                    clientBatch.clear();
                }
            }

            if (!clientBatch.isEmpty()) {
                clientSnapshotRepository.saveAll(clientBatch);
                clientBatch.clear();
            }

            if (clients.hasNext()) {
                pageable = clients.nextPageable();
                clients = clientRepository.findAllByIsActiveIsTrue(pageable);
            } else {
                break;
            }
        }

//        pageable = PageRequest.of(0, 50);
//        Page<Branch> branches = branchRepository.findAllByIsActiveIsTrue(pageable);
//        while (clients.hasContent()) {
//            List<ClientSnapshot> clientBatch = new ArrayList<>(50);
//            for (bran client : clients) {
//                clientBatch.add(ClientSnapshot.builder()
//                        .cnpj(client.getCnpj())
//                        .build());
//
//                if (clientBatch.size() >= 50) {
//                    clientSnapshotRepository.saveAll(clientBatch);
//                    clientBatch.clear();
//                }
//            }
//
//            if (!clientBatch.isEmpty()) {
//                clientSnapshotRepository.saveAll(clientBatch);
//                clientBatch.clear();
//            }
//
//            if (clients.hasNext()) {
//                pageable = clients.nextPageable();
//                clients = clientRepository.findAllByIsActiveIsTrue(pageable);
//            } else {
//                break;
//            }
//        }
        /*

        branch <- apenas isActive == true
        user <- apenas responsaveis de contratos
        provider <- apenas isActive == ACTIVE
        supplier <- apenas isActive == ACTIVE
        subcontractor <- apenas isActive == ACTIVE
        employee
        contract <- apenas isActive != PENDING
        contract supplier
        contract subcontractor
        employee contract
        document matrix group
        document matrix subgroup
        document matrix
        document
        document employee
        document supplier
        document subcontractor
        contract document
         */
    }
}
