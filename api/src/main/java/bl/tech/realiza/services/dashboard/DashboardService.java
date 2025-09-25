package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.*;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixGroup;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.enums.*;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import bl.tech.realiza.domains.services.snapshots.clients.ClientSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractDocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.employee.DocumentEmployeeSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixGroupSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.user.UserSnapshot;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.ForbiddenException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixGroupRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.clients.BranchSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.clients.ClientSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.contract.ContractDocumentSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.contract.ContractProviderSubcontractorSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.contract.ContractProviderSupplierSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.contract.ContractSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.documents.DocumentSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.documents.employee.DocumentEmployeeSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix.DocumentMatrixGroupSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix.DocumentMatrixSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.documents.provider.DocumentProviderSubcontractorSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.documents.provider.DocumentProviderSupplierSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.employees.EmployeeSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.providers.ProviderSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.providers.ProviderSubcontractorSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.providers.ProviderSupplierSnapshotRepository;
import bl.tech.realiza.gateways.repositories.services.snapshots.user.UserSnapshotRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.dashboard.DashboardFiltersRequestDto;
import bl.tech.realiza.gateways.responses.dashboard.*;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.interfaces.users.security.CrudPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.documents.Document.*;
import static bl.tech.realiza.domains.documents.Document.Status.*;
import static bl.tech.realiza.domains.documents.Document.Status.PENDENTE;
import static bl.tech.realiza.domains.employees.Employee.Situation.*;
import static bl.tech.realiza.domains.enums.ContractStatusEnum.*;
import static bl.tech.realiza.domains.enums.RiskLevel.*;
import static bl.tech.realiza.gateways.responses.dashboard.DashboardFiltersResponse.*;
import static java.lang.Math.*;

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
    private final BranchSnapshotRepository branchSnapshotRepository;
    private final UserRepository userRepository;
    private final UserSnapshotRepository userSnapshotRepository;
    private final ProviderSupplierSnapshotRepository providerSupplierSnapshotRepository;
    private final ProviderSubcontractorSnapshotRepository providerSubcontractorSnapshotRepository;
    private final EmployeeSnapshotRepository employeeSnapshotRepository;
    private final ContractProviderSupplierSnapshotRepository contractProviderSupplierSnapshotRepository;
    private final ContractProviderSubcontractorRepository contractProviderSubcontractorRepository;
    private final ContractProviderSubcontractorSnapshotRepository contractProviderSubcontractorSnapshotRepository;
    private final DocumentMatrixGroupRepository documentMatrixGroupRepository;
    private final DocumentMatrixGroupSnapshotRepository documentMatrixGroupSnapshotRepository;
//    private final DocumentMatrixSubgroupRepository documentMatrixSubgroupRepository;
//    private final DocumentMatrixSubgroupSnapshotRepository documentMatrixSubgroupSnapshotRepository;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentMatrixSnapshotRepository documentMatrixSnapshotRepository;
    private final DocumentProviderSupplierSnapshotRepository documentProviderSupplierSnapshotRepository;
    private final DocumentProviderSubcontractorSnapshotRepository documentProviderSubcontractorSnapshotRepository;
    private final DocumentEmployeeSnapshotRepository documentEmployeeSnapshotRepository;
    private final ContractDocumentRepository contractDocumentRepository;
    private final DocumentSnapshotRepository documentSnapshotRepository;
    private final ContractSnapshotRepository contractSnapshotRepository;
    private final ContractDocumentSnapshotRepository contractDocumentSnapshotRepository;
    private final ProviderSnapshotRepository providerSnapshotRepository;
    private final CrudPermission crudPermission;

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
            adherence = round(adherence * 100.0) / 100.0;

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

//    public DashboardGeneralDetailsResponseDto getGeneralDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
//        if (JwtService.getAuthenticatedUserId() != null) {
//            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
//                    .orElseThrow(() -> new NotFoundException("User not found"));
//            if (!crudPermission.hasPermission(user, PermissionTypeEnum.DASHBOARD, PermissionSubTypeEnum.GENERAL, DocumentTypeEnum.NONE)) {
//                throw new ForbiddenException("Not enough permissions");
//            }
//        } else {
//            throw new ForbiddenException("Not authenticated user");
//        }
//        clientRepository.findById(clientId)
//                .orElseThrow(() -> new NotFoundException("Client not found"));
//        List<String> branchIds = new ArrayList<>();
//        List<String> providerIds = new ArrayList<>();
//        List<String> documentTypes = new ArrayList<>();
//        List<String> responsibleIds = new ArrayList<>();
//        List<ContractStatusEnum> activeContract = new ArrayList<>();
//        List<Status> statuses = new ArrayList<>();
//        List<String> documentTitles = new ArrayList<>();
//        if (dashboardFiltersRequestDto != null) {
//            branchIds = dashboardFiltersRequestDto.getBranchIds() != null
//                    ? dashboardFiltersRequestDto.getBranchIds()
//                    : new ArrayList<>();
//            providerIds = dashboardFiltersRequestDto.getProviderIds() != null
//                    ? dashboardFiltersRequestDto.getProviderIds()
//                    : new ArrayList<>();
//            documentTypes = dashboardFiltersRequestDto.getDocumentTypes() != null
//                    ? dashboardFiltersRequestDto.getDocumentTypes()
//                    : new ArrayList<>();
//            responsibleIds = dashboardFiltersRequestDto.getResponsibleIds() != null
//                    ? dashboardFiltersRequestDto.getResponsibleIds()
//                    : new ArrayList<>();
//            activeContract = dashboardFiltersRequestDto.getActiveContract() != null
//                    ? dashboardFiltersRequestDto.getActiveContract()
//                    : new ArrayList<>();
//            statuses = dashboardFiltersRequestDto.getStatuses() != null
//                    ? dashboardFiltersRequestDto.getStatuses()
//                    : new ArrayList<>();
//            documentTitles = dashboardFiltersRequestDto.getDocumentTitles() != null
//                    ? dashboardFiltersRequestDto.getDocumentTitles()
//                    : new ArrayList<>();
//        }
//        if (activeContract.isEmpty()) {
//            activeContract = new ArrayList<>();
//            activeContract.add(ACTIVE);
//        }
//        // quantidade de fornecedores
//        Long supplierQuantity = providerSupplierRepository.countByClientIdAndIsActive(clientId);
//
//        // quantidade de contratos
//        Long contractQuantity = contractProviderSupplierRepository.countByClientIdAndIsActive(clientId, activeContract);
//
//        // funcionários alocados
//        Long allocatedEmployeeQuantity = employeeRepository.countEmployeeSupplierByClientIdAndAllocated(clientId, ALOCADO)
//                + employeeRepository.countEmployeeSubcontractorByClientIdAndAllocated(clientId, ALOCADO);
//
//        // conformidade
//        Double conformity = null;
//        Object[] conformityValuesSupplier = null;
//        Object[] conformityValuesSubcontractor = null;
//        if (branchIds.isEmpty()) {
//            conformityValuesSupplier = documentRepository.countTotalAndConformitySupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(clientId,
//                    providerIds,
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//            conformityValuesSubcontractor = documentRepository.countTotalAndConformitySubcontractorByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(clientId,
//                    providerIds,
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//        } else {
//            conformityValuesSupplier = documentRepository.countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(branchIds,
//                    providerIds,
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//            conformityValuesSubcontractor = documentRepository.countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(branchIds,
//                    providerIds,
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//        }
//        Long totalConformity = getSafeLong(conformityValuesSupplier, 0) + getSafeLong(conformityValuesSubcontractor, 0);
//        Long conformityTrue = getSafeLong(conformityValuesSupplier, 1) + getSafeLong(conformityValuesSubcontractor, 1);
//
//        conformity = totalConformity > 0
//                ? new BigDecimal(conformityTrue * 100.0 / totalConformity).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
//
//        // para cada type selecionado, quantidade de documentos com status
//        List<DashboardGeneralDetailsResponseDto.TypeStatus> documentStatus = new ArrayList<>();
//        List<DashboardGeneralDetailsResponseDto.Exemption> documentExemption = new ArrayList<>();
//
//        if (documentTypes.isEmpty()) {
//            documentTypes = documentRepository.findDistinctDocumentType();
//        }
//        for (String type : documentTypes) {
//            List<DashboardGeneralDetailsResponseDto.Status> statusList = new ArrayList<>();
//            if (statuses.isEmpty()) {
//                statuses = Arrays.asList(Status.values());
//            }
//            for (Status status : statuses) {
//                int supplier = 0;
//                int subcontract = 0;
//                if (!branchIds.isEmpty()) {
//                    supplier = documentRepository.countSupplierByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(branchIds,
//                            providerIds,
//                            type,
//                            status,
//                            responsibleIds,
//                            documentTitles).intValue();
//                    subcontract = documentRepository.countSubcontractorByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(branchIds,
//                            providerIds,
//                            type,
//                            status,
//                            responsibleIds,
//                            documentTitles).intValue();
//                } else {
//                    supplier = documentRepository.countSupplierByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(clientId,
//                            providerIds,
//                            type,
//                            status,
//                            responsibleIds,
//                            documentTitles).intValue();
//                    subcontract = documentRepository.countSubcontractorByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(clientId,
//                            providerIds,
//                            type,
//                            status,
//                            responsibleIds,
//                            documentTitles).intValue();
//                }
//                statusList.add(DashboardGeneralDetailsResponseDto.Status.builder()
//                        .quantity(supplier + subcontract)
//                        .status(status)
//                        .build());
//
//            }
//            long approvedIa = 0;
//            long reprovedIa = 0;
//            DashboardGeneralDetailsResponseDto.Status statusApprovedIA = statusList.stream()
//                    .filter(status -> status.getStatus() == APROVADO_IA)
//                    .findFirst()
//                    .orElse(null);
//            DashboardGeneralDetailsResponseDto.Status statusReprovedIA = statusList.stream()
//                    .filter(status -> status.getStatus() == REPROVADO_IA)
//                    .findFirst()
//                    .orElse(null);
//            DashboardGeneralDetailsResponseDto.Status statusUnderAnalysis = statusList.stream()
//                    .filter(status -> status.getStatus() == EM_ANALISE)
//                    .findFirst()
//                    .orElse(null);
//            if (statusApprovedIA != null) {
//                approvedIa = statusApprovedIA.getQuantity().longValue();
//            }
//            if (statusReprovedIA != null) {
//                reprovedIa = statusReprovedIA.getQuantity().longValue();
//            }
//            if (statusUnderAnalysis != null) {
//                long newQuantity = statusUnderAnalysis.getQuantity().longValue() + approvedIa + reprovedIa;
//                statusUnderAnalysis.setQuantity(Math.toIntExact(newQuantity));
//            }
//            statusList.removeIf(status -> status.getStatus() == APROVADO_IA || status.getStatus() == REPROVADO_IA);
//
//            documentStatus.add(DashboardGeneralDetailsResponseDto.TypeStatus.builder()
//                    .name(type)
//                    .status(statusList)
//                    .build());
//        }
//
//        // ranking de pendencias
//        List<DashboardGeneralDetailsResponseDto.Pending> pendingRanking = new ArrayList<>();
//        List<String> allBranches = branchRepository.findAllBranchIdsByClientId(clientId);
//
//        for (String branchId : allBranches) {
//            Branch branch = branchRepository.findById(branchId)
//                    .orElseThrow(() -> new NotFoundException("Branch not found"));
//            Double adherenceBranch = null;
//            Double conformityBranch = null;
//            List<String> newBranchIds = new ArrayList<>();
//            newBranchIds.add(branchId);
//
//            Object[] adherenceBranchSupplierValuesRaw = documentRepository.countTotalAndAdherenceSupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
//                    null,
//                    null,
//                    documentTypes,
//                    documentTitles);
//            Object[] adherenceBranchSubcontractorValuesRaw = documentRepository.countTotalAndAdherenceSubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
//                    null,
//                    null,
//                    documentTypes,
//                    documentTitles);
//            Object[] conformityBranchSupplierValuesRaw = documentRepository.countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
//                    null,
//                    null,
//                    documentTypes,
//                    documentTitles);
//            Object[] conformityBranchSubcontractorValuesRaw = documentRepository.countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(newBranchIds,
//                    null,
//                    null,
//                    documentTypes,
//                    documentTitles);
//
//            Object[] adherenceBranchSupplierValues = (Object[]) adherenceBranchSupplierValuesRaw[0];
//            Object[] adherenceBranchSubcontractorValues = (Object[]) adherenceBranchSubcontractorValuesRaw[0];
//            Object[] conformityBranchSupplierValues = (Object[]) conformityBranchSupplierValuesRaw[0];
//            Object[] conformityBranchSubcontractorValues = (Object[]) conformityBranchSubcontractorValuesRaw[0];
//
//            Long totalAdherenceBranch = getSafeLong(adherenceBranchSupplierValues, 0) + getSafeLong(adherenceBranchSubcontractorValues, 0);
//            Long adherenceBranchTrue = getSafeLong(adherenceBranchSupplierValues, 1) + getSafeLong(adherenceBranchSubcontractorValues, 1);
//            Long totalConformityBranch = getSafeLong(conformityBranchSupplierValues, 0) + getSafeLong(conformityBranchSubcontractorValues, 0);
//            Long conformityBranchTrue = getSafeLong(conformityBranchSupplierValues, 1) + getSafeLong(conformityBranchSubcontractorValues, 1);
//            Long nonConformityBranchTrue = (totalConformityBranch - conformityBranchTrue);
//
//            adherenceBranch = totalAdherenceBranch > 0
//                    ? new BigDecimal(adherenceBranchTrue * 100.0 / totalAdherenceBranch).setScale(2, RoundingMode.HALF_UP).doubleValue()
//                    : 100;
//
//            conformityBranch = totalConformityBranch > 0
//                    ? new BigDecimal(conformityBranchTrue * 100.0 / totalConformityBranch).setScale(2, RoundingMode.HALF_UP).doubleValue()
//                    : 100;
//
//            DashboardGeneralDetailsResponseDto.Conformity level;
//            if (conformityBranch < 60) {
//                level = DashboardGeneralDetailsResponseDto.Conformity.RISKY;
//            } else if (conformityBranch < 75) {
//                level = DashboardGeneralDetailsResponseDto.Conformity.ATTENTION;
//            } else if (conformityBranch < 90) {
//                level = DashboardGeneralDetailsResponseDto.Conformity.NORMAL;
//            } else {
//                level = DashboardGeneralDetailsResponseDto.Conformity.OK;
//            }
//
//            pendingRanking.add(DashboardGeneralDetailsResponseDto.Pending.builder()
//                    .corporateName(branch.getName())
//                    .cnpj(branch.getCnpj())
//                    .adherence(adherenceBranch)
//                    .conformity(conformityBranch)
//                    .nonConformingDocumentQuantity(nonConformityBranchTrue.intValue())
//                    .conformityLevel(level)
//                    .build());
//        }
//
//        return DashboardGeneralDetailsResponseDto.builder()
//                .supplierQuantity(supplierQuantity)
//                .contractQuantity(contractQuantity)
//                .allocatedEmployeeQuantity(allocatedEmployeeQuantity)
//                .conformity(conformity)
//                .documentStatus(documentStatus)
//                .documentExemption(documentExemption)
//                .pendingRanking(pendingRanking)
//                .build();
//    }
//
//    public List<DashboardProviderDetailsResponseDto> getProviderDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
//        if (JwtService.getAuthenticatedUserId() != null) {
//            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
//                    .orElseThrow(() -> new NotFoundException("User not found"));
//            if (!crudPermission.hasPermission(user,
//                    PermissionTypeEnum.DASHBOARD,
//                    PermissionSubTypeEnum.PROVIDER,
//                    DocumentTypeEnum.NONE)) {
//                throw new ForbiddenException("Not enough permissions");
//            }
//        } else {
//            throw new ForbiddenException("Not authenticated user");
//        }
//
//        List<String> branchIds = dashboardFiltersRequestDto != null
//                ? (dashboardFiltersRequestDto.getBranchIds() != null
//                    ? dashboardFiltersRequestDto.getBranchIds()
//                    : new ArrayList<>() )
//                : new ArrayList<>();
//        List<String> documentTypes = dashboardFiltersRequestDto != null
//                ? (dashboardFiltersRequestDto.getDocumentTypes() != null
//                    ? dashboardFiltersRequestDto.getDocumentTypes()
//                    : new ArrayList<>() )
//                : new ArrayList<>();
//        List<String> responsibleIds = dashboardFiltersRequestDto != null
//                ? (dashboardFiltersRequestDto.getResponsibleIds() != null
//                    ? dashboardFiltersRequestDto.getResponsibleIds()
//                    : new ArrayList<>() )
//                : new ArrayList<>();
//        List<String> documentTitles = dashboardFiltersRequestDto != null
//                ? (dashboardFiltersRequestDto.getDocumentTitles() != null
//                    ? dashboardFiltersRequestDto.getDocumentTitles()
//                    : new ArrayList<>() )
//                : new ArrayList<>();
//
//        List<DashboardProviderDetailsResponseDto> responseDtos = new ArrayList<>();
//        List<ProviderSupplier> providerSuppliers = new ArrayList<>();
//        List<ProviderSubcontractor> providerSubcontractors = new ArrayList<>();
//        Double adherenceProvider = null;
//        Double conformityProvider = null;
//        Object[] adherenceProviderValues = null;
//        Object[] conformityProviderValues = null;
//        if (branchIds.isEmpty()) {
//            providerSuppliers = providerSupplierRepository.findAllByClientIdAndContractStatusAndIsActiveIsTrue(clientId, ACTIVE);
//            providerSubcontractors = providerSubcontractorRepository.findAllByContractSupplierClientIdAndContractStatusAndIsActiveIsTrue(clientId, ACTIVE);
//        } else {
//            providerSuppliers = providerSupplierRepository.findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrue(branchIds,responsibleIds, ACTIVE);
//            providerSubcontractors = providerSubcontractorRepository.findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrue(branchIds,responsibleIds, ACTIVE);
//        }
//        for (ProviderSupplier providerSupplier : providerSuppliers ) {
//            adherenceProviderValues = documentRepository.countTotalAndAdherenceByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSupplier.getIdProvider(),
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//            conformityProviderValues = documentRepository.countTotalAndConformityByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSupplier.getIdProvider(),
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//
//            Long totalAdherenceProvider = getSafeLong(adherenceProviderValues,0);
//            Long adherenceProviderTrue = getSafeLong(adherenceProviderValues,1);
//            Long nonAdherenceProviderTrue = (totalAdherenceProvider - adherenceProviderTrue);
//            Long totalConformityProvider = getSafeLong(conformityProviderValues,0);
//            Long conformityProviderTrue = getSafeLong(conformityProviderValues,1);
//            Long nonConformityProviderTrue = (totalConformityProvider - conformityProviderTrue);
//            if (totalAdherenceProvider.equals(totalConformityProvider)) {
//                log.info("Values not match in provider supplier id {}",providerSupplier.getIdProvider());
//            }
//
//            adherenceProvider = totalAdherenceProvider > 0
//                    ? new BigDecimal(adherenceProviderTrue * 100.0 / totalAdherenceProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
//
//            conformityProvider = totalConformityProvider > 0
//                    ? new BigDecimal(conformityProviderTrue * 100.0 / totalConformityProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
//
//            DashboardProviderDetailsResponseDto.Conformity conformityRange;
//            if (conformityProvider < 60) {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.RISKY;
//            } else if (conformityProvider < 75) {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.ATTENTION;
//            } else if (conformityProvider < 90) {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.NORMAL;
//            } else {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.OK;
//            }
//
//            responseDtos.add(
//                    DashboardProviderDetailsResponseDto.builder()
//                            .corporateName(providerSupplier.getCorporateName())
//                            .cnpj(providerSupplier.getCnpj())
//                            .totalDocumentQuantity(totalAdherenceProvider)
//                            .adherenceQuantity(adherenceProviderTrue)
//                            .nonAdherenceQuantity(nonAdherenceProviderTrue)
//                            .conformityQuantity(conformityProviderTrue)
//                            .nonConformityQuantity(nonConformityProviderTrue)
//                            .adherence(adherenceProvider)
//                            .conformity(conformityProvider)
//                            .conformityRange(conformityRange)
//                            .build()
//            );
//        }
//        for (ProviderSubcontractor providerSubcontractor : providerSubcontractors ) {
//            adherenceProviderValues = documentRepository.countTotalAndAdherenceByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSubcontractor.getIdProvider(),
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//            conformityProviderValues = documentRepository.countTotalAndConformityByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(providerSubcontractor.getIdProvider(),
//                    responsibleIds,
//                    documentTypes,
//                    documentTitles);
//
//            Long totalAdherenceProvider = (Long) adherenceProviderValues[0];
//            Long adherenceProviderTrue = (Long) adherenceProviderValues[1];
//            Long nonAdherenceProviderTrue = (totalAdherenceProvider - adherenceProviderTrue);
//            Long totalConformityProvider = (Long) conformityProviderValues[0];
//            Long conformityProviderTrue = (Long) conformityProviderValues[1];
//            Long nonConformityProviderTrue = (totalConformityProvider - conformityProviderTrue);
//            if (totalAdherenceProvider.equals(totalConformityProvider)) {
//                log.info("Values not match in provider subcontractor id {}",providerSubcontractor.getIdProvider());
//            }
//
//            adherenceProvider = totalAdherenceProvider > 0
//                    ? new BigDecimal(adherenceProviderTrue * 100.0 / totalAdherenceProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
//
//            conformityProvider = totalConformityProvider > 0
//                    ? new BigDecimal(conformityProviderTrue * 100.0 / totalConformityProvider).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
//
//            DashboardProviderDetailsResponseDto.Conformity conformityRange;
//            if (conformityProvider < 60) {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.RISKY;
//            } else if (conformityProvider < 75) {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.ATTENTION;
//            } else if (conformityProvider < 90) {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.NORMAL;
//            } else {
//                conformityRange = DashboardProviderDetailsResponseDto.Conformity.OK;
//            }
//
//            responseDtos.add(
//                    DashboardProviderDetailsResponseDto.builder()
//                            .corporateName(providerSubcontractor.getCorporateName())
//                            .cnpj(providerSubcontractor.getCnpj())
//                            .totalDocumentQuantity(totalAdherenceProvider)
//                            .adherenceQuantity(adherenceProviderTrue)
//                            .nonAdherenceQuantity(nonAdherenceProviderTrue)
//                            .conformityQuantity(conformityProviderTrue)
//                            .nonConformityQuantity(nonConformityProviderTrue)
//                            .adherence(adherenceProvider)
//                            .conformity(conformityProvider)
//                            .conformityRange(conformityRange)
//                            .build()
//            );
//        }
//        return responseDtos;
//    }
//
//    public DashboardDocumentStatusResponseDto getDocumentStatusInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
//        if (JwtService.getAuthenticatedUserId() != null) {
//            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
//                    .orElseThrow(() -> new NotFoundException("User not found"));
//            if (!crudPermission.hasPermission(user,
//                    PermissionTypeEnum.DASHBOARD,
//                    PermissionSubTypeEnum.DOCUMENT,
//                    DocumentTypeEnum.NONE)) {
//                throw new ForbiddenException("Not enough permissions");
//            }
//        } else {
//            throw new ForbiddenException("Not authenticated user");
//        }
//        DashboardDocumentStatusResponseDto responseDto = DashboardDocumentStatusResponseDto.builder().build();
//        List<String> branchIds = new ArrayList<>();
//        List<String> providerIds = new ArrayList<>();
//        List<String> documentTypes = new ArrayList<>();
//        List<String> responsibleIds = new ArrayList<>();
//        List<ContractStatusEnum> activeContract = new ArrayList<>();
//        List<Status> statuses = new ArrayList<>();
//        List<String> documentTitles = new ArrayList<>();
//        if (dashboardFiltersRequestDto != null) {
//            branchIds = dashboardFiltersRequestDto.getBranchIds() != null
//                    ? dashboardFiltersRequestDto.getBranchIds()
//                    : new ArrayList<>();
//            providerIds = dashboardFiltersRequestDto.getProviderIds() != null
//                    ? dashboardFiltersRequestDto.getProviderIds()
//                    : new ArrayList<>();
//            documentTypes = dashboardFiltersRequestDto.getDocumentTypes() != null
//                    ? dashboardFiltersRequestDto.getDocumentTypes()
//                    : new ArrayList<>();
//            responsibleIds = dashboardFiltersRequestDto.getResponsibleIds() != null
//                    ? dashboardFiltersRequestDto.getResponsibleIds()
//                    : new ArrayList<>();
//            activeContract = dashboardFiltersRequestDto.getActiveContract() != null
//                    ? dashboardFiltersRequestDto.getActiveContract()
//                    : new ArrayList<>();
//            statuses = dashboardFiltersRequestDto.getStatuses() != null
//                    ? dashboardFiltersRequestDto.getStatuses()
//                    : new ArrayList<>();
//            documentTitles = dashboardFiltersRequestDto.getDocumentTitles() != null
//                    ? dashboardFiltersRequestDto.getDocumentTitles()
//                    : new ArrayList<>();
//        }
//        List<Document> documentsSupplier = new ArrayList<>();
//        List<Document> documentsSubcontractor = new ArrayList<>();
//        // find all documents by filters
//        if (branchIds.isEmpty()) {
//            documentsSupplier = documentRepository.findAllSupplierByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles);
//
//            documentsSubcontractor = documentRepository.findAllSubcontractorByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles);
//        } else {
//            documentsSupplier = documentRepository.findAllSupplierByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles);
//
//            documentsSubcontractor = documentRepository.findAllSubcontractorByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles);
//        }
//
//        // find all adherent documents by filters
//        long total = documentsSupplier.size() + documentsSubcontractor.size();
//        long adherentSupplier = documentsSupplier.stream()
//                .filter(Document::getAdherent)
//                .toList()
//                .size();
//        long adherentSubcontractor = documentsSubcontractor.stream()
//                .filter(Document::getAdherent)
//                .toList()
//                .size();
//        responseDto.setAdherentDocumentsQuantity(adherentSupplier + adherentSubcontractor);
//        responseDto.setNonAdherentDocumentsQuantity(total - responseDto.getAdherentDocumentsQuantity());
//
//        // find all conforming documents by filters
//        long conformingSupplier = documentsSupplier.stream()
//                .filter(Document::getConforming)
//                .toList()
//                .size();
//        long conformingSubcontractor = documentsSubcontractor.stream()
//                .filter(Document::getConforming)
//                .toList()
//                .size();
//        responseDto.setConformingDocumentsQuantity(conformingSupplier + conformingSubcontractor);
//        responseDto.setNonConformingDocumentsQuantity(total - responseDto.getConformingDocumentsQuantity());
//
//        // list infos by status
//        responseDto.setDocumentStatus(new ArrayList<>());
//        for (Document.Status status : Document.Status.values()) {
//            List<Document> documentSupplierStatus = documentsSupplier.stream()
//                    .filter(document -> document.getStatus().equals(status))
//                    .toList();
//            List<Document> documentSubcontractorStatus = documentsSubcontractor.stream()
//                    .filter(document -> document.getStatus().equals(status))
//                    .toList();
//            long totalStatus = documentSupplierStatus.size() + documentSubcontractorStatus.size();
//
//            Double percentage = total > 0
//                    ? new BigDecimal(totalStatus * 100.0 / total).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
//
//            DashboardDocumentStatusResponseDto.Status statusResponse = DashboardDocumentStatusResponseDto.Status.builder()
//                    .status(status)
//                    .adherent(status != PENDENTE && status != VENCIDO)
//                    .conforming(status == APROVADO)
//                    .quantity(totalStatus)
//                    .percentage(percentage)
//                    .build();
//            responseDto.getDocumentStatus().add(statusResponse);
//        }
//        // show all adherent and non-adherent
//
//        // show all conforming and non-conforming
//        // find all status and infos by filters
//        return responseDto;
//    }
//
//    public Page<DashboardDocumentDetailsResponseDto> getDocumentDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto, Pageable pageable) {
//        if (JwtService.getAuthenticatedUserId() != null) {
//            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
//                    .orElseThrow(() -> new NotFoundException("User not found"));
//            if (!crudPermission.hasPermission(user,
//                    PermissionTypeEnum.DASHBOARD,
//                    PermissionSubTypeEnum.DOCUMENT_DETAIL,
//                    DocumentTypeEnum.NONE)) {
//                throw new ForbiddenException("Not enough permissions");
//            }
//        } else {
//            throw new ForbiddenException("Not authenticated user");
//        }
//        DashboardDocumentDetailsResponseDto responseDto = DashboardDocumentDetailsResponseDto.builder().build();
//        List<String> branchIds = new ArrayList<>();
//        List<String> providerIds = new ArrayList<>();
//        List<String> documentTypes = new ArrayList<>();
//        List<String> responsibleIds = new ArrayList<>();
//        List<ContractStatusEnum> activeContract = new ArrayList<>();
//        List<Status> statuses = new ArrayList<>();
//        List<String> documentTitles = new ArrayList<>();
//        if (dashboardFiltersRequestDto != null) {
//            branchIds = dashboardFiltersRequestDto.getBranchIds() != null
//                    ? dashboardFiltersRequestDto.getBranchIds()
//                    : new ArrayList<>();
//            providerIds = dashboardFiltersRequestDto.getProviderIds() != null
//                    ? dashboardFiltersRequestDto.getProviderIds()
//                    : new ArrayList<>();
//            documentTypes = dashboardFiltersRequestDto.getDocumentTypes() != null
//                    ? dashboardFiltersRequestDto.getDocumentTypes()
//                    : new ArrayList<>();
//            responsibleIds = dashboardFiltersRequestDto.getResponsibleIds() != null
//                    ? dashboardFiltersRequestDto.getResponsibleIds()
//                    : new ArrayList<>();
//            activeContract = dashboardFiltersRequestDto.getActiveContract() != null
//                    ? dashboardFiltersRequestDto.getActiveContract()
//                    : new ArrayList<>();
//            statuses = dashboardFiltersRequestDto.getStatuses() != null
//                    ? dashboardFiltersRequestDto.getStatuses()
//                    : new ArrayList<>();
//            documentTitles = dashboardFiltersRequestDto.getDocumentTitles() != null
//                    ? dashboardFiltersRequestDto.getDocumentTitles()
//                    : new ArrayList<>();
//        }
//        Page<Document> documentsSupplier = null;
//        Page<Document> documentsSubcontractor = null;
//        // find all documents by filters
//        if (!branchIds.isEmpty()) {
//            documentsSupplier = documentRepository.findAllSupplierByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles,
//                    pageable);
//
//            documentsSubcontractor = documentRepository.findAllSubcontractorByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles,
//                    pageable);
//        } else {
//            documentsSupplier = documentRepository.findAllSupplierByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles,
//                    pageable);
//
//            documentsSubcontractor = documentRepository.findAllSubcontractorByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(clientId,
//                    providerIds,
//                    documentTypes,
//                    responsibleIds,
//                    activeContract,
//                    statuses,
//                    documentTitles,
//                    pageable);
//        }
//        List<Document> combinedDocuments = new ArrayList<>();
//        combinedDocuments.addAll(documentsSupplier.getContent());
//        combinedDocuments.addAll(documentsSubcontractor.getContent());
//        combinedDocuments.sort(Comparator.comparing(Document::getTitle));
//        List<Document> limitedDocuments = combinedDocuments.stream()
//                .limit(pageable.getPageSize())
//                .collect(Collectors.toList());
//
//        Page<Document> paginatedDocuments = new PageImpl<>(limitedDocuments, pageable, combinedDocuments.size());
//
//        return toDetailsPageDto(paginatedDocuments);
//    }
    
    public DashboardGeneralDetailsResponseDto getGeneralDetailsInfo(String clientId, DashboardFiltersRequestDto filters) {
        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (!crudPermission.hasPermission(user, PermissionTypeEnum.DASHBOARD, PermissionSubTypeEnum.GENERAL, DocumentTypeEnum.NONE)) {
                throw new ForbiddenException("Not enough permissions");
            }
        } else {
            throw new ForbiddenException("Not authenticated user");
        }
        clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        List<String> documentTypes = new ArrayList<>();
        List<ContractStatusEnum> activeContract = new ArrayList<>();
        List<Status> statuses = new ArrayList<>();
        if (filters != null) {
            documentTypes = filters.getDocumentTypes() != null
                    ? filters.getDocumentTypes()
                    : new ArrayList<>();
            activeContract = filters.getActiveContract() != null
                    ? filters.getActiveContract()
                    : new ArrayList<>();
            statuses = filters.getStatuses() != null
                    ? filters.getStatuses()
                    : new ArrayList<>();
        }
        if (activeContract.isEmpty()) {
            activeContract = new ArrayList<>();
            activeContract.add(ACTIVE);
        }
        Specification<Document> documentSpecifications = getDocumentSpecifications(filters, clientId);
        // quantidade de fornecedores
        Long supplierQuantity = providerSupplierRepository.countByClientIdAndIsActive(clientId);

        // quantidade de contratos
        Long contractQuantity = contractProviderSupplierRepository.countByClientIdAndIsActive(clientId, activeContract);

        // funcionários alocados
        Long allocatedEmployeeQuantity = employeeRepository.countEmployeeSupplierByClientIdAndAllocated(clientId, ALOCADO)
                + employeeRepository.countEmployeeSubcontractorByClientIdAndAllocated(clientId, ALOCADO);

        // conformidade
        Double conformity = null;
        long totalDocuments = documentRepository.count(documentSpecifications);
        long conformityTrue = documentRepository.count(documentSpecifications.and(DashboardDocumentSpecification.byConformingIsTrue()));
        long adherenceTrue = documentRepository.count(documentSpecifications.and(DashboardDocumentSpecification.byAdherenceIsTrue()));

        conformity = totalDocuments > 0
                ? new BigDecimal(conformityTrue * 100.0 / totalDocuments).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

        // para cada type selecionado, quantidade de documentos com status
        List<DashboardGeneralDetailsResponseDto.TypeStatus> documentStatus = new ArrayList<>();
        List<DashboardGeneralDetailsResponseDto.Exemption> documentExemption = new ArrayList<>();

        if (filters != null && filters.getDocumentTypes() != null && filters.getDocumentTypes().isEmpty()) {
            documentTypes = documentRepository.findDistinctDocumentType();
        }
        for (String type : documentTypes) {
            List<DashboardGeneralDetailsResponseDto.Status> statusList = new ArrayList<>();
            if (statuses.isEmpty()) {
                statuses = Arrays.asList(Status.values());
            }
            for (Status status : statuses) {
                statusList.add(DashboardGeneralDetailsResponseDto.Status.builder()
                        .quantity(toIntExact(documentRepository.count(documentSpecifications)))
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
                statusUnderAnalysis.setQuantity(toIntExact(newQuantity));
            }
            statusList.removeIf(status -> status.getStatus() == APROVADO_IA || status.getStatus() == REPROVADO_IA);

            documentStatus.add(DashboardGeneralDetailsResponseDto.TypeStatus.builder()
                    .name(type)
                    .status(statusList)
                    .build());
        }

        // ranking de pendencias
        List<DashboardGeneralDetailsResponseDto.Pending> pendingRanking = new ArrayList<>();
        List<String> allBranchIds = branchRepository.findAllBranchIdsByClientId(clientId);

        for (String branchId : allBranchIds) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new NotFoundException("Branch not found"));
            Double adherenceBranch = null;
            Double conformityBranch = null;
            DashboardFiltersRequestDto branchFilter = new DashboardFiltersRequestDto();
            Specification<Document> branchDocumentSpecifications = null;
            if (filters != null) {
                branchFilter = filters;
                if (branchFilter.getBranchIds() != null && !branchFilter.getBranchIds().isEmpty()) {
                    branchFilter.getBranchIds().clear();
                    branchFilter.getBranchIds().add(branchId);
                    branchDocumentSpecifications = getDocumentSpecifications(branchFilter, clientId);
                } else {
                    List<String> branchIdList = new ArrayList<>();
                    branchIdList.add(branchId);
                    branchFilter.setBranchIds(branchIdList);
                    branchDocumentSpecifications = getDocumentSpecifications(branchFilter, clientId);
                }
            } else {
                branchDocumentSpecifications = getDocumentSpecifications(branchFilter, clientId);
            }

            long totalBranchDocuments = documentRepository.count(branchDocumentSpecifications);
            long conformityBranchTrue = documentRepository.count(branchDocumentSpecifications.and(DashboardDocumentSpecification.byConformingIsTrue()));
            long conformityBranchFalse = totalBranchDocuments - conformityBranchTrue;
            long adherenceBranchTrue = documentRepository.count(branchDocumentSpecifications.and(DashboardDocumentSpecification.byAdherenceIsTrue()));
            long adherenceBranchFalse = totalBranchDocuments - adherenceBranchTrue;

            adherenceBranch = totalBranchDocuments > 0
                    ? new BigDecimal(adherenceBranchTrue * 100.0 / totalBranchDocuments)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 100;

            conformityBranch = totalBranchDocuments > 0
                    ? new BigDecimal(conformityBranchTrue * 100.0 / totalBranchDocuments)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 100;

            RiskLevel conformityLevel;
            if (conformityBranch < 60) {
                conformityLevel = RISKY;
            } else if (conformityBranch < 75) {
                conformityLevel = ATTENTION;
            } else if (conformityBranch < 90) {
                conformityLevel = NORMAL;
            } else {
                conformityLevel = OK;
            }

            RiskLevel adherenceLevel;
            if (adherenceBranch < 60) {
                adherenceLevel = RISKY;
            } else if (adherenceBranch < 75) {
                adherenceLevel = ATTENTION;
            } else if (adherenceBranch < 90) {
                adherenceLevel = NORMAL;
            } else {
                adherenceLevel = OK;
            }

            pendingRanking.add(DashboardGeneralDetailsResponseDto.Pending.builder()
                    .corporateName(branch.getName())
                    .cnpj(branch.getCnpj())
                    .adherence(adherenceBranch)
                    .nonAdherentDocumentQuantity(toIntExact(adherenceBranchFalse))
                    .adherenceLevel(adherenceLevel)
                    .conformity(conformityBranch)
                    .nonConformingDocumentQuantity(toIntExact(conformityBranchFalse))
                    .conformityLevel(conformityLevel)
                    .build());
        }

        return DashboardGeneralDetailsResponseDto.builder()
                .supplierQuantity(supplierQuantity)
                .contractQuantity(contractQuantity)
                .allocatedEmployeeQuantity(allocatedEmployeeQuantity)
                .conformity(conformity)
                .totalDocuments(totalDocuments)
                .adherent(adherenceTrue)
                .nonAdherent(totalDocuments - adherenceTrue)
                .conforming(conformityTrue)
                .nonConforming(totalDocuments - conformityTrue)
                .documentStatus(documentStatus)
                .documentExemption(documentExemption)
                .pendingRanking(pendingRanking)
                .build();
    }

    public List<DashboardProviderDetailsResponseDto> getProviderDetailsInfo(String clientId, DashboardFiltersRequestDto filters) {
        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (!crudPermission.hasPermission(user,
                    PermissionTypeEnum.DASHBOARD,
                    PermissionSubTypeEnum.PROVIDER,
                    DocumentTypeEnum.NONE)) {
                throw new ForbiddenException("Not enough permissions");
            }
        } else {
            throw new ForbiddenException("Not authenticated user");
        }
        List<DashboardProviderDetailsResponseDto> responseDtos = new ArrayList<>();
        Double adherenceProvider = null;
        Double conformityProvider = null;
        List<ContractStatusEnum> activeContract = new ArrayList<>();
        if (filters != null) {
            activeContract = filters.getActiveContract() != null
                    ? filters.getActiveContract()
                    : new ArrayList<>();
        }
        if (activeContract.isEmpty()) {
            activeContract = new ArrayList<>();
            activeContract.add(ACTIVE);
        }
        Specification<Provider> providerSpecifications = getProviderSpecifications(filters, clientId);
        List<ProviderSupplier> providerSuppliers = providerSupplierRepository.findAll((Sort) providerSpecifications);
        List<ProviderSubcontractor> providerSubcontractors = providerSubcontractorRepository.findAll((Sort) providerSpecifications);
        for (ProviderSupplier providerSupplier : providerSuppliers ) {
            DashboardFiltersRequestDto filtersProvider = new DashboardFiltersRequestDto();
            filtersProvider = filters;
            if (filtersProvider != null) {
                if (filtersProvider.getProviderIds() != null) {
                    filtersProvider.getProviderIds().clear();
                    filtersProvider.getProviderIds().add(providerSupplier.getIdProvider());
                } else {
                    filtersProvider.setProviderIds(new ArrayList<>());
                    filtersProvider.getProviderIds().add(providerSupplier.getIdProvider());
                }
            }
            Specification<Document> documentSpecifications = getDocumentSpecifications(filtersProvider, clientId);
            Long totalDocuments = documentRepository.count(documentSpecifications);
            Long adherenceProviderTrue = documentRepository.count(documentSpecifications.and(DashboardDocumentSpecification.byAdherenceIsTrue()));
            Long nonAdherenceProviderTrue = (totalDocuments - adherenceProviderTrue);
            Long conformityProviderTrue = documentRepository.count(documentSpecifications.and(DashboardDocumentSpecification.byConformingIsTrue()));
            Long nonConformityProviderTrue = (totalDocuments - conformityProviderTrue);

            adherenceProvider = totalDocuments > 0
                    ? new BigDecimal(adherenceProviderTrue * 100.0 / totalDocuments).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            conformityProvider = totalDocuments > 0
                    ? new BigDecimal(conformityProviderTrue * 100.0 / totalDocuments).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

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

            Long employeeQuantity = employeeRepository.countAllBySupplier_IdProvider(providerSupplier.getIdProvider());

            responseDtos.add(
                    DashboardProviderDetailsResponseDto.builder()
                            .corporateName(providerSupplier.getCorporateName())
                            .cnpj(providerSupplier.getCnpj())
                            .totalDocumentQuantity(totalDocuments)
                            .adherenceQuantity(adherenceProviderTrue)
                            .nonAdherenceQuantity(nonAdherenceProviderTrue)
                            .conformityQuantity(conformityProviderTrue)
                            .nonConformityQuantity(nonConformityProviderTrue)
                            .adherence(adherenceProvider)
                            .conformity(conformityProvider)
                            .conformityRange(conformityRange)
                            .employeeQuantity(employeeQuantity)
                            .build()
            );
        }
        for (ProviderSubcontractor providerSubcontractor : providerSubcontractors ) {
            DashboardFiltersRequestDto filtersProvider = new DashboardFiltersRequestDto();
            filtersProvider = filters;
            if (filtersProvider != null) {
                if (filtersProvider.getProviderIds() != null) {
                    filtersProvider.getProviderIds().clear();
                    filtersProvider.getProviderIds().add(providerSubcontractor.getIdProvider());
                } else {
                    filtersProvider.setProviderIds(new ArrayList<>());
                    filtersProvider.getProviderIds().add(providerSubcontractor.getIdProvider());
                }
            }
            Specification<Document> documentSpecifications = getDocumentSpecifications(filtersProvider, clientId);
            Long totalDocuments = documentRepository.count(documentSpecifications);
            Long adherenceProviderTrue = documentRepository.count(documentSpecifications.and(DashboardDocumentSpecification.byAdherenceIsTrue()));
            Long nonAdherenceProviderTrue = (totalDocuments - adherenceProviderTrue);
            Long conformityProviderTrue = documentRepository.count(documentSpecifications.and(DashboardDocumentSpecification.byConformingIsTrue()));
            Long nonConformityProviderTrue = (totalDocuments - conformityProviderTrue);

            adherenceProvider = totalDocuments > 0
                    ? new BigDecimal(adherenceProviderTrue * 100.0 / totalDocuments).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

            conformityProvider = totalDocuments > 0
                    ? new BigDecimal(conformityProviderTrue * 100.0 / totalDocuments).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

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

            Long employeeQuantity = employeeRepository.countAllBySubcontract_IdProvider(providerSubcontractor.getIdProvider());

            responseDtos.add(
                    DashboardProviderDetailsResponseDto.builder()
                            .corporateName(providerSubcontractor.getCorporateName())
                            .cnpj(providerSubcontractor.getCnpj())
                            .totalDocumentQuantity(totalDocuments)
                            .adherenceQuantity(adherenceProviderTrue)
                            .nonAdherenceQuantity(nonAdherenceProviderTrue)
                            .conformityQuantity(conformityProviderTrue)
                            .nonConformityQuantity(nonConformityProviderTrue)
                            .adherence(adherenceProvider)
                            .conformity(conformityProvider)
                            .conformityRange(conformityRange)
                            .employeeQuantity(employeeQuantity)
                            .build()
            );
        }
        return responseDtos;
    }

    public DashboardDocumentStatusResponseDto getDocumentStatusInfo(String clientId, DashboardFiltersRequestDto filters) {
        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (!crudPermission.hasPermission(user,
                    PermissionTypeEnum.DASHBOARD,
                    PermissionSubTypeEnum.DOCUMENT,
                    DocumentTypeEnum.NONE)) {
                throw new ForbiddenException("Not enough permissions");
            }
        } else {
            throw new ForbiddenException("Not authenticated user");
        }
        DashboardDocumentStatusResponseDto responseDto = DashboardDocumentStatusResponseDto.builder().build();
        List<String> branchIds = new ArrayList<>();
        List<String> providerIds = new ArrayList<>();
        List<String> documentTypes = new ArrayList<>();
        List<String> responsibleIds = new ArrayList<>();
        List<ContractStatusEnum> activeContract = new ArrayList<>();
        List<Status> statuses = new ArrayList<>();
        List<String> documentTitles = new ArrayList<>();
        if (filters != null) {
            branchIds = filters.getBranchIds() != null
                    ? filters.getBranchIds()
                    : new ArrayList<>();
            providerIds = filters.getProviderIds() != null
                    ? filters.getProviderIds()
                    : new ArrayList<>();
            documentTypes = filters.getDocumentTypes() != null
                    ? filters.getDocumentTypes()
                    : new ArrayList<>();
            responsibleIds = filters.getResponsibleIds() != null
                    ? filters.getResponsibleIds()
                    : new ArrayList<>();
            activeContract = filters.getActiveContract() != null
                    ? filters.getActiveContract()
                    : new ArrayList<>();
            statuses = filters.getStatuses() != null
                    ? filters.getStatuses()
                    : new ArrayList<>();
            documentTitles = filters.getDocumentTitles() != null
                    ? filters.getDocumentTitles()
                    : new ArrayList<>();
        }
        Specification<Document> documentSpecification = getDocumentSpecifications(filters, clientId);
        List<Document> documents = documentRepository.findAll(documentSpecification);

        // find all adherent documents by filters
        long total = documents.size();
        long adherent = documents.stream()
                .filter(Document::getAdherent)
                .toList()
                .size();

        responseDto.setAdherentDocumentsQuantity(adherent);
        responseDto.setNonAdherentDocumentsQuantity(total - adherent);

        // find all conforming documents by filters
        long conforming = documents.stream()
                .filter(Document::getConforming)
                .toList()
                .size();
        responseDto.setConformingDocumentsQuantity(conforming);
        responseDto.setNonConformingDocumentsQuantity(total - conforming);

        // list infos by status
        responseDto.setDocumentStatus(new ArrayList<>());
        for (Document.Status status : Document.Status.values()) {
            List<Document> documentStatuses = documents.stream()
                    .filter(document -> document.getStatus().equals(status))
                    .toList();
            long totalStatus = documentStatuses.size();

            Double percentage = total > 0
                    ? new BigDecimal(totalStatus * 100.0 / total)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 0;

            DashboardDocumentStatusResponseDto.Status statusResponse = DashboardDocumentStatusResponseDto.Status.builder()
                    .status(status)
                    .adherent(status != PENDENTE && status != VENCIDO)
                    .conforming(status == APROVADO)
                    .quantity(totalStatus)
                    .percentage(percentage)
                    .build();
            responseDto.getDocumentStatus().add(statusResponse);
        }

        return responseDto;
    }

    public Page<DashboardDocumentDetailsResponseDto> getDocumentDetailsInfo(String clientId, DashboardFiltersRequestDto filters, Pageable pageable) {
        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (!crudPermission.hasPermission(user,
                    PermissionTypeEnum.DASHBOARD,
                    PermissionSubTypeEnum.DOCUMENT_DETAIL,
                    DocumentTypeEnum.NONE)) {
                throw new ForbiddenException("Not enough permissions");
            }
        } else {
            throw new ForbiddenException("Not authenticated user");
        }

        Specification<Document> spec = getDocumentSpecifications(filters, clientId);

        Page<Document> paginatedDocuments = documentRepository.findAll(spec, pageable);

        return toDetailsPageDto(paginatedDocuments);
    }

    private long getSafeLong(Object[] array, int index) {
        if (array != null && array.length > index && array[index] instanceof Number number) {
            return number.longValue();
        }
        return 0L;
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
//                .documentSubgroupName(document.getDocumentMatrix() != null
//                        ? (document.getDocumentMatrix().getSubGroup() != null
//                            ? document.getDocumentMatrix().getSubGroup().getSubgroupName()
//                            : null)
//                        : null)
                .documentGroupName(document.getDocumentMatrix() != null
                        ? document.getDocumentMatrix().getGroup().getGroupName()
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

    // TODO verificar bug no take snapshot
    public void takeSnapshot(SnapshotFrequencyEnum frequency) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Client> clients = clientRepository.findAllByIsActiveIsTrue(pageable);
        while (clients.hasContent()) {
            List<ClientSnapshot> clientBatch = new ArrayList<>(50);
            for (Client client : clients) {
                clientBatch.add(ClientSnapshot.builder()
                                .id(SnapshotId.builder()
                                        .id(client.getIdClient())
                                        .snapshotDate(Date.from(LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                                        .frequency(frequency)
                                        .build())
                                .cnpj(client.getCnpj())
                                .corporateName(client.getCorporateName())
                                .tradeName(client.getTradeName())
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

        pageable = PageRequest.of(0, 50);
        Page<Branch> branches = branchRepository.findAllByIsActiveIsTrue(pageable);
        while (branches.hasContent()) {
            List<BranchSnapshot> branchBatch = new ArrayList<>(50);
            for (Branch branch : branches) {
                ClientSnapshot clientSnapshot = clientSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(branch.getClient().getIdClient(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                                .orElseThrow(() -> new NotFoundException("Client not found"));
                branchBatch.add(BranchSnapshot.builder()
                                .id(SnapshotId.builder()
                                        .id(branch.getIdBranch())
                                        .snapshotDate(Date.from(LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                                        .frequency(frequency)
                                        .build())
                                .tradeName(branch.getName())
                                .cnpj(branch.getCnpj())
                                .client(clientSnapshot)
                        .build());

                if (branchBatch.size() >= 50) {
                    branchSnapshotRepository.saveAll(branchBatch);
                    branchBatch.clear();
                }
            }

            if (!branchBatch.isEmpty()) {
                branchSnapshotRepository.saveAll(branchBatch);
                branchBatch.clear();
            }

            if (branches.hasNext()) {
                pageable = branches.nextPageable();
                branches = branchRepository.findAllByIsActiveIsTrue(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<User> users = userRepository.findAllByContractsIsEmpty(false, pageable);
        while (users.hasContent()) {
            List<UserSnapshot> userBatch = new ArrayList<>(50);
            for (User user : users) {
                userBatch.add(UserSnapshot.builder()
                                .id(SnapshotId.builder()
                                        .id(user.getIdUser())
                                        .snapshotDate(Date.from(LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                                        .frequency(frequency)
                                        .build())
                                .firstName(user.getFirstName())
                                .surname(user.getSurname())
                                .email(user.getEmail())
                        .build());

                if (userBatch.size() >= 50) {
                    userSnapshotRepository.saveAll(userBatch);
                    userBatch.clear();
                }
            }

            if (!userBatch.isEmpty()) {
                userSnapshotRepository.saveAll(userBatch);
                userBatch.clear();
            }

            if (users.hasNext()) {
                pageable = users.nextPageable();
                users = userRepository.findAllByContractsIsEmpty(false, pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<ProviderSupplier> suppliers = providerSupplierRepository.findAllByIsActiveIsTrue(pageable);
        while (suppliers.hasContent()) {
            List<ProviderSupplierSnapshot> providerBatch = new ArrayList<>(50);
            for (ProviderSupplier provider : suppliers) {
                providerBatch.add(ProviderSupplierSnapshot.builder()
                                .id(SnapshotId.builder()
                                        .id(provider.getIdProvider())
                                        .snapshotDate(Date.from(LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                                        .frequency(frequency)
                                        .build())
                                .corporateName(provider.getCorporateName())
                                .tradeName(provider.getTradeName())
                                .cnpj(provider.getCnpj())
                        .build());

                if (providerBatch.size() >= 50) {
                    providerSupplierSnapshotRepository.saveAll(providerBatch);
                    providerBatch.clear();
                }
            }

            if (!providerBatch.isEmpty()) {
                providerSupplierSnapshotRepository.saveAll(providerBatch);
                providerBatch.clear();
            }

            if (suppliers.hasNext()) {
                pageable = suppliers.nextPageable();
                suppliers = providerSupplierRepository.findAllByIsActiveIsTrue(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<ProviderSubcontractor> subcontractors = providerSubcontractorRepository.findAllByIsActiveIsTrue(pageable);
        while (subcontractors.hasContent()) {
            List<ProviderSubcontractorSnapshot> providerBatch = new ArrayList<>(50);
            for (ProviderSubcontractor provider : subcontractors) {
                providerBatch.add(ProviderSubcontractorSnapshot.builder()
                        .id(SnapshotId.builder()
                                .id(provider.getIdProvider())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                        .corporateName(provider.getCorporateName())
                        .tradeName(provider.getTradeName())
                        .cnpj(provider.getCnpj())
                        .build());

                if (providerBatch.size() >= 50) {
                    providerSubcontractorSnapshotRepository.saveAll(providerBatch);
                    providerBatch.clear();
                }
            }

            if (!providerBatch.isEmpty()) {
                providerSubcontractorSnapshotRepository.saveAll(providerBatch);
                providerBatch.clear();
            }

            if (subcontractors.hasNext()) {
                pageable = subcontractors.nextPageable();
                subcontractors = providerSubcontractorRepository.findAllByIsActiveIsTrue(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<Employee> employees = employeeRepository.findAll(pageable);
        while (employees.hasContent()) {
            List<EmployeeSnapshot> employeeBatch = new ArrayList<>(50);
            for (Employee employee : employees) {
                ProviderSupplierSnapshot providerSupplierSnapshot = null;
                ProviderSubcontractorSnapshot providerSubcontractorSnapshot = null;
                if (employee.getSupplier() != null) {
                    providerSupplierSnapshot = providerSupplierSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(employee.getSupplier().getIdProvider(),
                                    Date.from(LocalDateTime.now()
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant()),
                                    frequency)
                            .orElse(null);
                } else if (employee.getSubcontract() != null) {
                    providerSubcontractorSnapshot = providerSubcontractorSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(employee.getSubcontract().getIdProvider(),
                                    Date.from(LocalDateTime.now()
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant()),
                                    frequency)
                            .orElse(null);
                }
                employeeBatch.add(EmployeeSnapshot.builder()
                                .id(SnapshotId.builder()
                                        .id(employee.getIdEmployee())
                                        .snapshotDate(Date.from(LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                                        .frequency(frequency)
                                        .build())
                                .name(employee.getName())
                                .surname(employee.getSurname())
                                .position(employee.getPosition().getTitle())
                                .cbo(employee.getCbo().getTitle())
                                .situation(employee.getSituation())
                                .supplier(providerSupplierSnapshot)
                                .subcontractor(providerSubcontractorSnapshot)
                        .build());

                if (employeeBatch.size() >= 50) {
                    employeeSnapshotRepository.saveAll(employeeBatch);
                    employeeBatch.clear();
                }
            }

            if (!employeeBatch.isEmpty()) {
                employeeSnapshotRepository.saveAll(employeeBatch);
                employeeBatch.clear();
            }

            if (employees.hasNext()) {
                pageable = employees.nextPageable();
                employees = employeeRepository.findAll(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<ContractProviderSupplier> contractSuppliers = contractProviderSupplierRepository.findAllByIsActiveIsNot(pageable, PENDING);
        while (contractSuppliers.hasContent()) {
            List<ContractProviderSupplierSnapshot> contractsBatch = new ArrayList<>(50);
            for (ContractProviderSupplier contract : contractSuppliers) {
                BranchSnapshot branchSnapshot = branchSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getBranch().getIdBranch(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Branch not found"));
                ProviderSupplierSnapshot providerSupplierSnapshot = providerSupplierSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getProviderSupplier().getIdProvider(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Supplier not found"));
                UserSnapshot responsible = userSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getResponsible().getIdUser(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Responsible not found"));
                List<EmployeeSnapshot> employeesInContract = employeeSnapshotRepository.findAllById_IdInAndId_SnapshotDateAndId_Frequency(contract.getEmployeeContracts().stream().map(ContractEmployee::getEmployee).toList().stream()
                        .map(Employee::getIdEmployee)
                        .collect(Collectors.toList()),
                        Date.from(LocalDateTime.now()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()),
                        frequency);
                contractsBatch.add(ContractProviderSupplierSnapshot.builder()
                                .id(SnapshotId.builder()
                                        .id(contract.getIdContract())
                                        .snapshotDate(Date.from(LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                                        .frequency(frequency)
                                        .build())
                                .reference(contract.getContractReference())
                                .serviceType(contract.getServiceTypeBranch().getTitle())
                                .status(contract.getStatus())
                                .start(contract.getDateStart())
                                .finish(contract.getEndDate())
                                .responsible(responsible)
                                .employees(employeesInContract)
                                .branch(branchSnapshot)
                                .supplier(providerSupplierSnapshot)
                        .build());

                if (contractsBatch.size() >= 50) {
                    contractProviderSupplierSnapshotRepository.saveAll(contractsBatch);
                    contractsBatch.clear();
                }
            }

            if (!contractsBatch.isEmpty()) {
                contractProviderSupplierSnapshotRepository.saveAll(contractsBatch);
                contractsBatch.clear();
            }

            if (contractSuppliers.hasNext()) {
                pageable = contractSuppliers.nextPageable();
                contractSuppliers = contractProviderSupplierRepository.findAll(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<ContractProviderSubcontractor> contractSubcontractors = contractProviderSubcontractorRepository.findAllByIsActiveIsNot(pageable, PENDING);
        while (contractSubcontractors.hasContent()) {
            List<ContractProviderSubcontractorSnapshot> contractsBatch = new ArrayList<>(50);
            for (ContractProviderSubcontractor contract : contractSubcontractors) {
                ProviderSupplierSnapshot providerSupplierSnapshot = providerSupplierSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getProviderSupplier().getIdProvider(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Supplier not found"));
                ProviderSubcontractorSnapshot providerSubcontractorSnapshot = providerSubcontractorSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getProviderSubcontractor().getIdProvider(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Subcontractor not found"));
                UserSnapshot responsible = userSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getResponsible().getIdUser(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Responsible not found"));
                ContractProviderSupplierSnapshot contractProviderSupplierSnapshot = contractProviderSupplierSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contract.getContractProviderSupplier().getIdContract(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Contract not found"));
                List<EmployeeSnapshot> employeesInContract = employeeSnapshotRepository.findAllById_IdInAndId_SnapshotDateAndId_Frequency(contract.getEmployeeContracts().stream().map(ContractEmployee::getEmployee).collect(Collectors.toList()).stream()
                        .map(Employee::getIdEmployee)
                        .collect(Collectors.toList()),
                        Date.from(LocalDateTime.now()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()),
                        frequency);
                contractsBatch.add(ContractProviderSubcontractorSnapshot.builder()
                        .id(SnapshotId.builder()
                                .id(contract.getIdContract())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                        .reference(contract.getContractReference())
                        .serviceType(contract.getServiceTypeBranch().getTitle())
                        .status(contract.getStatus())
                        .start(contract.getDateStart())
                        .finish(contract.getEndDate())
                        .responsible(responsible)
                        .supplier(providerSupplierSnapshot)
                        .employees(employeesInContract)
                                .subcontractor(providerSubcontractorSnapshot)
                                .contractSupplier(contractProviderSupplierSnapshot)
                        .build());

                if (contractsBatch.size() >= 50) {
                    contractProviderSubcontractorSnapshotRepository.saveAll(contractsBatch);
                    contractsBatch.clear();
                }
            }

            if (!contractsBatch.isEmpty()) {
                contractProviderSubcontractorSnapshotRepository.saveAll(contractsBatch);
                contractsBatch.clear();
            }

            if (contractSubcontractors.hasNext()) {
                pageable = contractSubcontractors.nextPageable();
                contractSubcontractors = contractProviderSubcontractorRepository.findAllByIsActiveIsNot(pageable, PENDING);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<DocumentMatrixGroup> groups = documentMatrixGroupRepository.findAll(pageable);
        while (groups.hasContent()) {
            List<DocumentMatrixGroupSnapshot> groupsBatch = new ArrayList<>(50);
            for (DocumentMatrixGroup group : groups) {
                groupsBatch.add(DocumentMatrixGroupSnapshot.builder()
                        .id(SnapshotId.builder()
                        .id(group.getIdDocumentGroup())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                                .name(group.getGroupName())
                        .build());

                if (groupsBatch.size() >= 50) {
                    documentMatrixGroupSnapshotRepository.saveAll(groupsBatch);
                    groupsBatch.clear();
                }
            }

            if (!groupsBatch.isEmpty()) {
                documentMatrixGroupSnapshotRepository.saveAll(groupsBatch);
                groupsBatch.clear();
            }

            if (groups.hasNext()) {
                pageable = groups.nextPageable();
                groups = documentMatrixGroupRepository.findAll(pageable);
            } else {
                break;
            }
        }

//        pageable = PageRequest.of(0, 50);
//        Page<DocumentMatrixSubgroup> subgroups = documentMatrixSubgroupRepository.findAll(pageable);
//        while (subgroups.hasContent()) {
//            List<DocumentMatrixSubgroupSnapshot> subgroupsBatch = new ArrayList<>(50);
//            for (DocumentMatrixSubgroup subgroup : subgroups) {
//                DocumentMatrixGroupSnapshot groupSnapshot = documentMatrixGroupSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(subgroup.getGroup().getIdDocumentGroup(),
//                                Date.from(LocalDateTime.now()
//                                        .atZone(ZoneId.systemDefault())
//                                        .toInstant()),
//                                frequency)
//                                .orElseThrow(() -> new NotFoundException("Subgroup not found"));
//                subgroupsBatch.add(DocumentMatrixSubgroupSnapshot.builder()
//                        .id(SnapshotId.builder()
//                                .id(subgroup.getIdDocumentSubgroup())
//                                .snapshotDate(Date.from(LocalDateTime.now()
//                                        .atZone(ZoneId.systemDefault())
//                                        .toInstant()))
//                                .frequency(frequency)
//                                .build())
//                                .name(subgroup.getSubgroupName())
//                                .group(groupSnapshot)
//                        .build());
//
//                if (subgroupsBatch.size() >= 50) {
//                    documentMatrixSubgroupSnapshotRepository.saveAll(subgroupsBatch);
//                    subgroupsBatch.clear();
//                }
//            }
//
//            if (!subgroupsBatch.isEmpty()) {
//                documentMatrixSubgroupSnapshotRepository.saveAll(subgroupsBatch);
//                subgroupsBatch.clear();
//            }
//
//            if (subgroups.hasNext()) {
//                pageable = subgroups.nextPageable();
//                subgroups = documentMatrixSubgroupRepository.findAll(pageable);
//            } else {
//                break;
//            }
//        }

        pageable = PageRequest.of(0, 50);
        Page<DocumentMatrix> documentsMatrix = documentMatrixRepository.findAll(pageable);
        while (documentsMatrix.hasContent()) {
            List<DocumentMatrixSnapshot> documentsMatrixBatch = new ArrayList<>(50);
            for (DocumentMatrix document : documentsMatrix) {
//                DocumentMatrixSubgroupSnapshot subgroupSnapshot = documentMatrixSubgroupSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getSubGroup().getIdDocumentSubgroup(),
//                                Date.from(LocalDateTime.now()
//                                        .atZone(ZoneId.systemDefault())
//                                        .toInstant()),
//                                frequency)
//                        .orElseThrow(() -> new NotFoundException("Subgroup not found"));
                DocumentMatrixGroupSnapshot groupSnapshot = documentMatrixGroupSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getGroup().getIdDocumentGroup(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Group not found"));
                documentsMatrixBatch.add(DocumentMatrixSnapshot.builder()
                        .id(SnapshotId.builder()
                        .id(document.getIdDocument())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                        .name(document.getName())
                                .type(document.getType())
                                .isUnique(document.getIsDocumentUnique())
                        .group(groupSnapshot)
                        .build());

                if (documentsMatrixBatch.size() >= 50) {
                    documentMatrixSnapshotRepository.saveAll(documentsMatrixBatch);
                    documentsMatrixBatch.clear();
                }
            }

            if (!documentsMatrixBatch.isEmpty()) {
                documentMatrixSnapshotRepository.saveAll(documentsMatrixBatch);
                documentsMatrixBatch.clear();
            }

            if (documentsMatrix.hasNext()) {
                pageable = documentsMatrix.nextPageable();
                documentsMatrix = documentMatrixRepository.findAll(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<DocumentProviderSupplier> documentsSupplier = documentProviderSupplierRepository.findAllByProviderSupplier_IsActive(pageable, true);
        while (documentsSupplier.hasContent()) {
            List<DocumentProviderSupplierSnapshot> documentsSupplierBatch = new ArrayList<>(50);
            for (DocumentProviderSupplier document : documentsSupplier) {
                DocumentMatrixSnapshot documentMatrixSnapshot = documentMatrixSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getDocumentMatrix().getIdDocument(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                                .orElseThrow(() -> new NotFoundException("Document Matrix not found"));
                ProviderSupplierSnapshot supplierSnapshot = providerSupplierSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getProviderSupplier().getIdProvider(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                                .orElseThrow(() -> new NotFoundException("Supplier not found"));
                documentsSupplierBatch.add(DocumentProviderSupplierSnapshot.builder()
                        .id(SnapshotId.builder()
                                .id(document.getIdDocumentation())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                                .title(document.getTitle())
                                .status(document.getStatus())
                                .type(document.getType())
                                .versionDate(document.getVersionDate())
                                .expirationDate(document.getExpirationDate())
                                .documentDate(document.getDocumentDate())
                                .lastCheck(document.getLastCheck())
                                .validity(document.getValidity())
                                .adherent(document.getAdherent())
                                .conforming(document.getConforming())
                                .doesBlock(document.getDoesBlock())
                                .documentMatrix(documentMatrixSnapshot)
                                .assignmentDate(document.getAssignmentDate())
                                .supplier(supplierSnapshot)
                        .build());

                if (documentsSupplierBatch.size() >= 50) {
                    documentProviderSupplierSnapshotRepository.saveAll(documentsSupplierBatch);
                    documentsSupplierBatch.clear();
                }
            }

            if (!documentsSupplierBatch.isEmpty()) {
                documentProviderSupplierSnapshotRepository.saveAll(documentsSupplierBatch);
                documentsSupplierBatch.clear();
            }

            if (documentsSupplier.hasNext()) {
                pageable = documentsSupplier.nextPageable();
                documentsSupplier = documentProviderSupplierRepository.findAll(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<DocumentProviderSubcontractor> documentsSubcontractor = documentProviderSubcontractorRepository.findAllByProviderSubcontractor_IsActive(pageable, true);
        while (documentsSubcontractor.hasContent()) {
            List<DocumentProviderSubcontractorSnapshot> documentsSubcontractorBatch = new ArrayList<>(50);
            for (DocumentProviderSubcontractor document : documentsSubcontractor) {
                DocumentMatrixSnapshot documentMatrixSnapshot = documentMatrixSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getDocumentMatrix().getIdDocument(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Document Matrix not found"));
                ProviderSubcontractorSnapshot subcontractorSnapshot = providerSubcontractorSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getProviderSubcontractor().getIdProvider(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Subcontractor not found"));
                documentsSubcontractorBatch.add(DocumentProviderSubcontractorSnapshot.builder()
                        .id(SnapshotId.builder()
                        .id(document.getIdDocumentation())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                        .title(document.getTitle())
                        .status(document.getStatus())
                        .type(document.getType())
                        .versionDate(document.getVersionDate())
                        .expirationDate(document.getExpirationDate())
                        .documentDate(document.getDocumentDate())
                        .lastCheck(document.getLastCheck())
                        .validity(document.getValidity())
                        .adherent(document.getAdherent())
                        .conforming(document.getConforming())
                        .doesBlock(document.getDoesBlock())
                        .documentMatrix(documentMatrixSnapshot)
                        .assignmentDate(document.getAssignmentDate())
                        .subcontractor(subcontractorSnapshot)
                        .build());

                if (documentsSubcontractorBatch.size() >= 50) {
                    documentProviderSubcontractorSnapshotRepository.saveAll(documentsSubcontractorBatch);
                    documentsSubcontractorBatch.clear();
                }
            }

            if (!documentsSubcontractorBatch.isEmpty()) {
                documentProviderSubcontractorSnapshotRepository.saveAll(documentsSubcontractorBatch);
                documentsSubcontractorBatch.clear();
            }

            if (documentsSubcontractor.hasNext()) {
                pageable = documentsSubcontractor.nextPageable();
                documentsSubcontractor = documentProviderSubcontractorRepository.findAll(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<DocumentEmployee> documentsEmployee = documentEmployeeRepository.findAll(pageable);
        while (documentsEmployee.hasContent()) {
            List<DocumentEmployeeSnapshot> documentsEmployeeBatch = new ArrayList<>(50);
            for (DocumentEmployee document : documentsEmployee) {
                DocumentMatrixSnapshot documentMatrixSnapshot = documentMatrixSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getDocumentMatrix().getIdDocument(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Document Matrix not found"));
                EmployeeSnapshot employeeSnapshot = employeeSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(document.getEmployee().getIdEmployee(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Employee not found"));
                documentsEmployeeBatch.add(DocumentEmployeeSnapshot.builder()
                        .id(SnapshotId.builder()
                        .id(document.getIdDocumentation())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                        .title(document.getTitle())
                        .status(document.getStatus())
                        .type(document.getType())
                        .frequency(frequency)
                        .versionDate(document.getVersionDate())
                        .expirationDate(document.getExpirationDate())
                        .documentDate(document.getDocumentDate())
                        .lastCheck(document.getLastCheck())
                        .validity(document.getValidity())
                        .adherent(document.getAdherent())
                        .conforming(document.getConforming())
                        .doesBlock(document.getDoesBlock())
                        .documentMatrix(documentMatrixSnapshot)
                        .assignmentDate(document.getAssignmentDate())
                        .employee(employeeSnapshot)
                        .build());

                if (documentsEmployeeBatch.size() >= 50) {
                    documentEmployeeSnapshotRepository.saveAll(documentsEmployeeBatch);
                    documentsEmployeeBatch.clear();
                }
            }

            if (!documentsEmployeeBatch.isEmpty()) {
                documentEmployeeSnapshotRepository.saveAll(documentsEmployeeBatch);
                documentsEmployeeBatch.clear();
            }

            if (documentsEmployee.hasNext()) {
                pageable = documentsEmployee.nextPageable();
                documentsEmployee = documentEmployeeRepository.findAll(pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 50);
        Page<ContractDocument> contractDocuments = contractDocumentRepository.findAllByContract_IsActiveIsNot(pageable, PENDING);
        while (contractDocuments.hasContent()) {
            List<ContractDocumentSnapshot> contractDocumentsBatch = new ArrayList<>(50);
            for (ContractDocument contractDocument : contractDocuments) {
                DocumentSnapshot documentSnapshot = documentSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contractDocument.getDocument().getIdDocumentation(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Document not found"));
                ContractSnapshot contractSnapshot = contractSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(contractDocument.getContract().getIdContract(),
                                Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()),
                                frequency)
                        .orElseThrow(() -> new NotFoundException("Contract not found"));
                contractDocumentsBatch.add(ContractDocumentSnapshot.builder()
                        .id(SnapshotId.builder()
                        .id(contractDocument.getId())
                                .snapshotDate(Date.from(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                                .frequency(frequency)
                                .build())
                                .status(contractDocument.getStatus())
                                .contract(contractSnapshot)
                                .document(documentSnapshot)
                        .build());

                if (contractDocumentsBatch.size() >= 50) {
                    contractDocumentSnapshotRepository.saveAll(contractDocumentsBatch);
                    contractDocumentsBatch.clear();
                }
            }

            if (!contractDocumentsBatch.isEmpty()) {
                contractDocumentSnapshotRepository.saveAll(contractDocumentsBatch);
                contractDocumentsBatch.clear();
            }

            if (contractDocuments.hasNext()) {
                pageable = contractDocuments.nextPageable();
                contractDocuments = contractDocumentRepository.findAllByContract_IsActiveIsNot(pageable, PENDING);
            } else {
                break;
            }
        }
    }

    public void deleteSnapshot() {
        Pageable pageable = PageRequest.of(0, 25);
        Page<ClientSnapshot> clientSnapshots = clientSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                Date.from(LocalDateTime.now()
                        .minusMonths(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                SnapshotFrequencyEnum.DAILY,
                pageable);
        while (clientSnapshots.hasContent()) {
            clientSnapshotRepository.deleteAllInBatch(clientSnapshots.getContent());
            if (clientSnapshots.hasNext()) {
                pageable = clientSnapshots.nextPageable();
                clientSnapshots = clientSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                        Date.from(LocalDateTime.now()
                                .minusMonths(1)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()),
                        SnapshotFrequencyEnum.DAILY,
                        pageable);
            } else {
                break;
            }
        }
        pageable = PageRequest.of(0, 25);
        Page<ProviderSnapshot> providerSnapshots = providerSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                Date.from(LocalDateTime.now()
                        .minusMonths(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                SnapshotFrequencyEnum.DAILY,
                pageable);
        while (providerSnapshots.hasContent()) {
            providerSnapshotRepository.deleteAllInBatch(providerSnapshots.getContent());
            if (providerSnapshots.hasNext()) {
                pageable = providerSnapshots.nextPageable();
                providerSnapshots = providerSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                        Date.from(LocalDateTime.now()
                                .minusMonths(1)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()),
                        SnapshotFrequencyEnum.DAILY,
                        pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 25);
        Page<UserSnapshot> userSnapshots = userSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                Date.from(LocalDateTime.now()
                        .minusMonths(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                SnapshotFrequencyEnum.DAILY,
                pageable);
        while (userSnapshots.hasContent()) {
            userSnapshotRepository.deleteAllInBatch(userSnapshots.getContent());
            if (userSnapshots.hasNext()) {
                pageable = userSnapshots.nextPageable();
                userSnapshots = userSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                        Date.from(LocalDateTime.now()
                                .minusMonths(1)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()),
                        SnapshotFrequencyEnum.DAILY,
                        pageable);
            } else {
                break;
            }
        }

        pageable = PageRequest.of(0, 25);
        Page<EmployeeSnapshot> employeeSnapshots = employeeSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                Date.from(LocalDateTime.now()
                        .minusMonths(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                SnapshotFrequencyEnum.DAILY,
                pageable);
        while (employeeSnapshots.hasContent()) {
            employeeSnapshotRepository.deleteAllInBatch(employeeSnapshots.getContent());
            if (employeeSnapshots.hasNext()) {
                pageable = employeeSnapshots.nextPageable();
                employeeSnapshots = employeeSnapshotRepository.findAllById_SnapshotDateBeforeAndId_Frequency(
                        Date.from(LocalDateTime.now()
                                .minusMonths(1)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()),
                        SnapshotFrequencyEnum.DAILY,
                        pageable);
            } else {
                break;
            }
        }
    }

    // TODO verificar funcionamento no get snapshot
    //      getGeneralDetailsInfoByDate
    //      getProviderDetailsInfoByDate
    //      getDocumentStatusInfoByDate
    //      getDocumentDetailsInfo
    public DashboardGeneralDetailsResponseDto getGeneralDetailsInfoByDate(String clientId, Date date, SnapshotFrequencyEnum frequency, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        clientSnapshotRepository.findById_IdAndId_SnapshotDateAndId_Frequency(clientId,
                date,
                frequency)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        List<String> branchIds = new ArrayList<>();
        List<String> providerIds = new ArrayList<>();
        List<String> documentTypes = new ArrayList<>();
        List<String> responsibleIds = new ArrayList<>();
        List<ContractStatusEnum> activeContract = new ArrayList<>();
        List<Status> statuses = new ArrayList<>();
        List<String> documentTitles = new ArrayList<>();
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
        if (activeContract.isEmpty()) {
            activeContract = new ArrayList<>();
            activeContract.add(ACTIVE);
        }
        // quantidade de fornecedores
        Long supplierQuantity = providerSupplierSnapshotRepository.countByClientIdDateAndFrequency(clientId,
                date,
                frequency);

        // quantidade de contratos
        Long contractQuantity = contractProviderSupplierSnapshotRepository.countByClientIdAndStatusInAndDateAndFrequency(clientId,
                activeContract,
                date,
                frequency);

        // funcionários alocados
        Long allocatedEmployeeQuantity = employeeSnapshotRepository.countEmployeeSupplierByClientIdAndSituationAndDateAndFrequency(clientId,
                ALOCADO,
                date,
                frequency)
                + employeeSnapshotRepository.countEmployeeSubcontractorByClientIdAndSituationAndDateAndFrequency(clientId,
                ALOCADO,
                date,
                frequency);

        // conformidade
        Double conformity = null;
        Object[] conformityValuesSupplier = null;
        Object[] conformityValuesSubcontractor = null;
        if (branchIds.isEmpty()) {
            conformityValuesSupplier = documentSnapshotRepository.countTotalAndConformitySupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(clientId,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            conformityValuesSubcontractor = documentSnapshotRepository.countTotalAndConformitySubcontractorByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(clientId,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
        } else {
            conformityValuesSupplier = documentSnapshotRepository.countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(branchIds,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            conformityValuesSubcontractor = documentSnapshotRepository.countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(branchIds,
                    providerIds,
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
        }
        Long totalConformity = getSafeLong(conformityValuesSupplier, 0) + getSafeLong(conformityValuesSubcontractor, 0);
        Long conformityTrue = getSafeLong(conformityValuesSupplier, 1) + getSafeLong(conformityValuesSubcontractor, 1);

        conformity = totalConformity > 0
                ? new BigDecimal(conformityTrue * 100.0 / totalConformity).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;

        // para cada type selecionado, quantidade de documentos com status
        List<DashboardGeneralDetailsResponseDto.TypeStatus> documentStatus = new ArrayList<>();
        List<DashboardGeneralDetailsResponseDto.Exemption> documentExemption = new ArrayList<>();

        if (documentTypes.isEmpty()) {
            documentTypes = documentSnapshotRepository.findDistinctDocumentType(date,frequency);
        }
        for (String type : documentTypes) {
            List<DashboardGeneralDetailsResponseDto.Status> statusList = new ArrayList<>();
            if (statuses.isEmpty()) {
                statuses = Arrays.asList(Status.values());
            }
            for (Status status : statuses) {
                int supplier = 0;
                int subcontract = 0;
                if (!branchIds.isEmpty()) {
                    supplier = documentSnapshotRepository.countSupplierByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(branchIds,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles,
                            date,
                            frequency).intValue();
                    subcontract = documentSnapshotRepository.countSubcontractorByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(branchIds,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles,
                            date,
                            frequency).intValue();
                } else {
                    supplier = documentSnapshotRepository.countSupplierByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(clientId,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles,
                            date,
                            frequency).intValue();
                    subcontract = documentSnapshotRepository.countSubcontractorByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(clientId,
                            providerIds,
                            type,
                            status,
                            responsibleIds,
                            documentTitles,
                            date,
                            frequency).intValue();
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
                statusUnderAnalysis.setQuantity(toIntExact(newQuantity));
            }
            statusList.removeIf(status -> status.getStatus() == APROVADO_IA || status.getStatus() == REPROVADO_IA);

            documentStatus.add(DashboardGeneralDetailsResponseDto.TypeStatus.builder()
                    .name(type)
                    .status(statusList)
                    .build());
        }

        // ranking de pendencias
        List<DashboardGeneralDetailsResponseDto.Pending> pendingRanking = new ArrayList<>();
        List<String> allBranches = branchSnapshotRepository.findAllBranchIdsByClientIdAndDateAndFrequency(clientId,date,frequency);

        for (String branchId : allBranches) {
            BranchSnapshot branch = branchSnapshotRepository.findById(branchId)
                    .orElseThrow(() -> new NotFoundException("Branch not found"));
            Double adherenceBranch = null;
            Double conformityBranch = null;
            List<String> newBranchIds = new ArrayList<>();
            newBranchIds.add(branchId);

            Object[] adherenceBranchSupplierValuesRaw = documentSnapshotRepository.countTotalAndAdherenceSupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            Object[] adherenceBranchSubcontractorValuesRaw = documentSnapshotRepository.countTotalAndAdherenceSubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            Object[] conformityBranchSupplierValuesRaw = documentSnapshotRepository.countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            Object[] conformityBranchSubcontractorValuesRaw = documentSnapshotRepository.countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(newBranchIds,
                    null,
                    null,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);

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

            RiskLevel level;
            if (conformityBranch < 60) {
                level = RiskLevel.RISKY;
            } else if (conformityBranch < 75) {
                level = RiskLevel.ATTENTION;
            } else if (conformityBranch < 90) {
                level = RiskLevel.NORMAL;
            } else {
                level = RiskLevel.OK;
            }

            pendingRanking.add(DashboardGeneralDetailsResponseDto.Pending.builder()
                    .corporateName(branch.getTradeName())
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

    public List<DashboardProviderDetailsResponseDto> getProviderDetailsInfoByDate(String clientId, Date date, SnapshotFrequencyEnum frequency, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
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
        List<ProviderSupplierSnapshot> providerSuppliers = new ArrayList<>();
        List<ProviderSubcontractorSnapshot> providerSubcontractors = new ArrayList<>();
        Double adherenceProvider = null;
        Double conformityProvider = null;
        Object[] adherenceProviderValues = null;
        Object[] conformityProviderValues = null;

        if (branchIds.isEmpty()) {
            providerSuppliers = providerSupplierSnapshotRepository.findAllByClientIdAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(clientId, ACTIVE, date, frequency);
            providerSubcontractors = providerSubcontractorSnapshotRepository.findAllByContractSupplierClientIdAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(clientId, ACTIVE, date, frequency);
        } else {
            providerSuppliers = providerSupplierSnapshotRepository.findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(branchIds,responsibleIds, ACTIVE, date, frequency);
            providerSubcontractors = providerSubcontractorSnapshotRepository.findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(branchIds,responsibleIds, ACTIVE, date, frequency);
        }
        for (ProviderSupplierSnapshot providerSupplier : providerSuppliers ) {
            adherenceProviderValues = documentSnapshotRepository.countTotalAndAdherenceByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(providerSupplier.getId().getId(),
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            conformityProviderValues = documentSnapshotRepository.countTotalAndConformityByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(providerSupplier.getId().getId(),
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);

            Long totalAdherenceProvider = getSafeLong(adherenceProviderValues,0);
            Long adherenceProviderTrue = getSafeLong(adherenceProviderValues,1);
            Long nonAdherenceProviderTrue = (totalAdherenceProvider - adherenceProviderTrue);
            Long totalConformityProvider = getSafeLong(conformityProviderValues,0);
            Long conformityProviderTrue = getSafeLong(conformityProviderValues,1);
            Long nonConformityProviderTrue = (totalConformityProvider - conformityProviderTrue);
            if (totalAdherenceProvider.equals(totalConformityProvider)) {
                log.info("Values not match in provider supplier id {}",providerSupplier.getId().getId());
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
        for (ProviderSubcontractorSnapshot providerSubcontractor : providerSubcontractors ) {
            adherenceProviderValues = documentSnapshotRepository.countTotalAndAdherenceByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(providerSubcontractor.getId().getId(),
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);
            conformityProviderValues = documentSnapshotRepository.countTotalAndConformityByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(providerSubcontractor.getId().getId(),
                    responsibleIds,
                    documentTypes,
                    documentTitles,
                    date,
                    frequency);

            Long totalAdherenceProvider = (Long) adherenceProviderValues[0];
            Long adherenceProviderTrue = (Long) adherenceProviderValues[1];
            Long nonAdherenceProviderTrue = (totalAdherenceProvider - adherenceProviderTrue);
            Long totalConformityProvider = (Long) conformityProviderValues[0];
            Long conformityProviderTrue = (Long) conformityProviderValues[1];
            Long nonConformityProviderTrue = (totalConformityProvider - conformityProviderTrue);
            if (totalAdherenceProvider.equals(totalConformityProvider)) {
                log.info("Values not match in provider subcontractor id {}",providerSubcontractor.getId().getId());
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

    public DashboardDocumentStatusResponseDto getDocumentStatusInfoByDate(String clientId, Date date, SnapshotFrequencyEnum frequency, DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        DashboardDocumentStatusResponseDto responseDto = DashboardDocumentStatusResponseDto.builder().build();
        List<String> branchIds = new ArrayList<>();
        List<String> providerIds = new ArrayList<>();
        List<String> documentTypes = new ArrayList<>();
        List<String> responsibleIds = new ArrayList<>();
        List<ContractStatusEnum> activeContract = new ArrayList<>();
        List<Status> statuses = new ArrayList<>();
        List<String> documentTitles = new ArrayList<>();
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
        List<DocumentSnapshot> documentsSupplier = new ArrayList<>();
        List<DocumentSnapshot> documentsSubcontractor = new ArrayList<>();
        // find all documents by filters
        if (branchIds.isEmpty()) {
            documentsSupplier = documentSnapshotRepository.findAllSupplierByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitlesAndDateAndFrequency(clientId,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    date,
                    frequency);

            documentsSubcontractor = documentSnapshotRepository.findAllSubcontractorByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitlesAndDateAndFrequency(clientId,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    date,
                    frequency);
        } else {
            documentsSupplier = documentSnapshotRepository.findAllSupplierByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitlesAndDateAndFrequency(branchIds,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    date,
                    frequency);

            documentsSubcontractor = documentSnapshotRepository.findAllSubcontractorByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(branchIds,
                    providerIds,
                    documentTypes,
                    responsibleIds,
                    activeContract,
                    statuses,
                    documentTitles,
                    date,
                    frequency);
        }

        // find all adherent documents by filters
        long total = documentsSupplier.size() + documentsSubcontractor.size();
        long adherentSupplier = documentsSupplier.stream()
                .filter(DocumentSnapshot::getAdherent)
                .toList()
                .size();
        long adherentSubcontractor = documentsSubcontractor.stream()
                .filter(DocumentSnapshot::getAdherent)
                .toList()
                .size();
        responseDto.setAdherentDocumentsQuantity(adherentSupplier + adherentSubcontractor);
        responseDto.setNonAdherentDocumentsQuantity(total - responseDto.getAdherentDocumentsQuantity());

        // find all conforming documents by filters
        long conformingSupplier = documentsSupplier.stream()
                .filter(DocumentSnapshot::getConforming)
                .toList()
                .size();
        long conformingSubcontractor = documentsSubcontractor.stream()
                .filter(DocumentSnapshot::getConforming)
                .toList()
                .size();
        responseDto.setConformingDocumentsQuantity(conformingSupplier + conformingSubcontractor);
        responseDto.setNonConformingDocumentsQuantity(total - responseDto.getConformingDocumentsQuantity());

        // list infos by status
        responseDto.setDocumentStatus(new ArrayList<>());
        for (Document.Status status : Document.Status.values()) {
            List<DocumentSnapshot> documentSupplierStatus = documentsSupplier.stream()
                    .filter(document -> document.getStatus().equals(status))
                    .toList();
            List<DocumentSnapshot> documentSubcontractorStatus = documentsSubcontractor.stream()
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
        // TODO show all adherent and non-adherent

        // TODO show all conforming and non-conforming
        // TODO find all status and infos by filters
        return responseDto;
    }

    public DashboardFiltersResponse getFiltersInfo(String clientId) {
        DashboardFiltersResponse response = builder().build();

        clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        // branches
        List<Branch> branches = branchRepository.findAllByClient_IdClientAndIsActiveIsTrue(clientId);
        List<FilterList> branchResponse = new ArrayList<>();
        for (Branch branch : branches) {
            branchResponse.add(FilterList.builder()
                            .id(branch.getIdBranch())
                            .name(branch.getName())
                    .build());
        }
        response.setBranches(branchResponse);

        // providers and responsibles
        List<ContractProviderSupplier> suppliers = contractProviderSupplierRepository.findAllByBranch_Client_IdClientAndStatusIsNot(clientId, DENIED);
        List<ContractProviderSubcontractor> subcontractors = contractProviderSubcontractorRepository.findAllByContractProviderSupplier_Branch_Client_IdClientAndStatusIsNot(clientId, DENIED);
        List<FilterList> providerResponse = new ArrayList<>();
        List<String> providerCnpjResponse = new ArrayList<>();
        List<FilterList> responsibleResponse = new ArrayList<>();
        List<FilterList> contractResponse = new ArrayList<>();
        List<FilterList> employeesResponse = new ArrayList<>();
        List<String> employeeCpfsResponse = new ArrayList<>();
        for (ContractProviderSupplier supplier : suppliers) {
            providerResponse.add(FilterList.builder()
                    .id(supplier.getProviderSupplier() != null
                            ? supplier.getProviderSupplier().getIdProvider()
                            : null)
                    .name(supplier.getProviderSupplier() != null
                            ? supplier.getProviderSupplier().getCorporateName()
                            : null)
                    .build());
            providerCnpjResponse.add(supplier.getProviderSupplier() != null
                    ? supplier.getProviderSupplier().getCnpj()
                    : null);
            responsibleResponse.add(FilterList.builder()
                    .id(supplier.getResponsible() != null
                            ? supplier.getResponsible().getIdUser()
                            : null)
                    .name(supplier.getResponsible() != null
                            ? supplier.getResponsible().getFullName()
                            : null)
                    .build());
            contractResponse.add(FilterList.builder()
                    .id(supplier.getIdContract())
                    .name(supplier.getContractReference())
                    .build());
            List<Employee> employees = supplier.getEmployeeContracts()
                    .stream().map(ContractEmployee::getEmployee)
                    .toList();
            for (Employee employee : employees) {
                employeesResponse.add(FilterList.builder()
                                .id(employee.getIdEmployee())
                                .name(employee.getFullName())
                        .build());
                if (employee instanceof EmployeeBrazilian brazilian) {
                    employeeCpfsResponse.add(brazilian.getCpf());
                }
            }
        }
        for (ContractProviderSubcontractor subcontractor : subcontractors) {
            providerResponse.add(FilterList.builder()
                    .id(subcontractor.getProviderSubcontractor() != null
                            ? subcontractor.getProviderSubcontractor().getIdProvider()
                            : null)
                    .name(subcontractor.getProviderSubcontractor() != null
                            ? subcontractor.getProviderSubcontractor().getCorporateName()
                            : null)
                    .build());
            providerCnpjResponse.add(subcontractor.getProviderSupplier() != null
                    ? subcontractor.getProviderSupplier().getCnpj()
                    : null);
            responsibleResponse.add(FilterList.builder()
                    .id(subcontractor.getResponsible() != null
                            ? subcontractor.getResponsible().getIdUser()
                            : null)
                    .name(subcontractor.getResponsible() != null
                            ? subcontractor.getResponsible().getFullName()
                            : null)
                    .build());
            contractResponse.add(FilterList.builder()
                    .id(subcontractor.getIdContract())
                    .name(subcontractor.getContractReference())
                    .build());
            List<Employee> employees = subcontractor.getEmployeeContracts()
                    .stream().map(ContractEmployee::getEmployee)
                    .toList();
            for (Employee employee : employees) {
                employeesResponse.add(FilterList.builder()
                        .id(employee.getIdEmployee())
                        .name(employee.getFullName())
                        .build());
                if (employee instanceof EmployeeBrazilian brazilian) {
                    employeeCpfsResponse.add(brazilian.getCpf());
                }
            }
        }
        response.setProviders(providerResponse);
        response.setResponsibles(responsibleResponse);
        response.setProviderCnpjs(providerCnpjResponse);
        response.setContracts(contractResponse);
        response.setEmployees(employeesResponse);
        response.setEmployeeCpfs(employeeCpfsResponse);

        // document types
        List<String> documentTypes = documentRepository.findDistinctDocumentType();
        response.setDocumentTypes(documentTypes);

        // document titles
        List<String> documentMatrix = documentMatrixRepository.findAllTitles();
        List<String> documentMatrixResponse = new ArrayList<>(documentMatrix);
        response.setDocumentTitles(documentMatrixResponse);


        // contract status
        List<ContractStatusEnum> contractStatus = new ArrayList<>(Arrays.asList(ContractStatusEnum.values()));
        response.setContractStatus(contractStatus);

        // document status
        List<Document.Status> documentStatus = new ArrayList<>(Arrays.asList(Document.Status.values()));
        response.setStatuses(documentStatus);

        List<Employee.Situation> employeeSituations = new ArrayList<>(Arrays.asList(Employee.Situation.values()));
        response.setEmployeeSituations(employeeSituations);

        List<Boolean> doesBlock = new ArrayList<>();
        doesBlock.add(Boolean.TRUE);
        doesBlock.add(Boolean.FALSE);
        response.setDocumentDoesBlock(doesBlock);

        List<DocumentValidityEnum> documentValidity = new ArrayList<>(Arrays.asList(DocumentValidityEnum.values()));
        response.setDocumentValidity(documentValidity);

        return response;
    }

    public Specification<Document> getDocumentSpecifications(DashboardFiltersRequestDto filters, String clientId) {
        Specification<Document> spec = Specification.where(null);

        if (filters != null) {
            if (filters.getBranchIds() != null && !filters.getBranchIds().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byBranchIds(filters.getBranchIds()));
            }

            if (filters.getProviderIds() != null && !filters.getProviderIds().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byProviderIds(filters.getProviderIds()));
            }

            if (filters.getDocumentTypes() != null && !filters.getDocumentTypes().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byDocumentTypes(filters.getDocumentTypes()));
            }

            if (filters.getResponsibleIds() != null && !filters.getResponsibleIds().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byResponsibleIds(filters.getResponsibleIds()));
            }

            if (filters.getActiveContract() != null && !filters.getActiveContract().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byContractStatus(filters.getActiveContract()));
            }

            if (filters.getStatuses() != null && !filters.getStatuses().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byStatuses(filters.getStatuses()));
            }

            if (filters.getDocumentTitles() != null && !filters.getDocumentTitles().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byTitles(filters.getDocumentTitles()));
            }

            if (filters.getProviderCnpjs() != null && !filters.getProviderCnpjs().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byProviderCnpjs(filters.getProviderCnpjs()));
            }

            if (filters.getContractIds() != null && !filters.getContractIds().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byContractIds(filters.getContractIds()));
            }

            if (filters.getEmployeeIds() != null && !filters.getEmployeeIds().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byEmployeeIds(filters.getEmployeeIds()));
            }

            if (filters.getEmployeeCpfs() != null && !filters.getEmployeeCpfs().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byEmployeeCpfs(filters.getEmployeeCpfs()));
            }

            if (filters.getEmployeeSituations() != null && !filters.getEmployeeSituations().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byEmployeeSituations(filters.getEmployeeSituations()));
            }

            if (filters.getDocumentDoesBlock() != null && !filters.getDocumentDoesBlock().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byDoesBlock(filters.getDocumentDoesBlock()));
            }

            if (filters.getDocumentValidity() != null && !filters.getDocumentValidity().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byValidities(filters.getDocumentValidity()));
            }

            if (filters.getDocumentUploadDate() != null && !filters.getDocumentUploadDate().isEmpty()) {
                spec = spec.and(DashboardDocumentSpecification.byUploadDates(filters.getDocumentUploadDate()));
            }
        }

        if (filters == null || filters.getBranchIds() == null || filters.getBranchIds().isEmpty()) {
            spec = spec.and(DashboardDocumentSpecification.byClientId(clientId));
        }

        return spec;
    }

    private Specification<Provider> getProviderSpecifications(DashboardFiltersRequestDto filters, String clientId) {
        Specification<Provider> spec = Specification.where(null);

        spec = spec.and(DashboardProviderSpecification.byIsActive(Boolean.TRUE));

        if (filters != null) {
            if (filters.getBranchIds() != null && !filters.getBranchIds().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byBranchIds(filters.getBranchIds()));
            }

            if (filters.getProviderIds() != null && !filters.getProviderIds().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byProviderIds(filters.getProviderIds()));
            }

            if (filters.getDocumentTypes() != null && !filters.getDocumentTypes().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byDocumentTypes(filters.getDocumentTypes()));
            }

            if (filters.getResponsibleIds() != null && !filters.getResponsibleIds().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byResponsibleIds(filters.getResponsibleIds()));
            }

            if (filters.getActiveContract() != null && !filters.getActiveContract().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byContractStatus(filters.getActiveContract()));
            }

            if (filters.getStatuses() != null && !filters.getStatuses().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byDocumentStatuses(filters.getStatuses()));
            }

            if (filters.getDocumentTitles() != null && !filters.getDocumentTitles().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byDocumentTitles(filters.getDocumentTitles()));
            }

            if (filters.getProviderCnpjs() != null && !filters.getProviderCnpjs().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byProviderCnpjs(filters.getProviderCnpjs()));
            }

            if (filters.getContractIds() != null && !filters.getContractIds().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byContractIds(filters.getContractIds()));
            }

            if (filters.getEmployeeIds() != null && !filters.getEmployeeIds().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byEmployeeIds(filters.getEmployeeIds()));
            }

            if (filters.getEmployeeCpfs() != null && !filters.getEmployeeCpfs().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byEmployeeCpfs(filters.getEmployeeCpfs()));
            }

            if (filters.getEmployeeSituations() != null && !filters.getEmployeeSituations().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byEmployeeSituations(filters.getEmployeeSituations()));
            }

            if (filters.getDocumentDoesBlock() != null && !filters.getDocumentDoesBlock().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byDoesBlock(filters.getDocumentDoesBlock()));
            }

            if (filters.getDocumentValidity() != null && !filters.getDocumentValidity().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byValidities(filters.getDocumentValidity()));
            }

            if (filters.getDocumentUploadDate() != null && !filters.getDocumentUploadDate().isEmpty()) {
                spec = spec.and(DashboardProviderSpecification.byUploadDates(filters.getDocumentUploadDate()));
            }
        }

        if (filters == null || filters.getBranchIds() == null || filters.getBranchIds().isEmpty()) {
            spec = spec.and(DashboardProviderSpecification.byClientId(clientId));
        }

        return spec;
    }
}
