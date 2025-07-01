package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static bl.tech.realiza.domains.contract.Contract.IsActive.*;
import static bl.tech.realiza.domains.documents.Document.*;
import static bl.tech.realiza.domains.documents.Document.Status.*;
import static bl.tech.realiza.domains.documents.Document.Status.PENDENTE;
import static bl.tech.realiza.domains.employees.Employee.Situation.*;

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
        activeContractQuantity = contractProviderSupplierRepository.countByBranch_IdBranchAndIsActiveAndFinishedIsFalse(branchId, ATIVADO).intValue();

        activeEmployeeQuantity = employeeRepository.countAllBySupplier_Branches_IdBranchAndSituation(branchId, ALOCADO).intValue()
        + employeeRepository.countAllBySupplier_Branches_IdBranchAndSituation(branchId, DESALOCADO).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(branchId, ALOCADO).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(branchId, DESALOCADO).intValue();

        // quantidade de fornecedores
        supplierQuantity = providerSupplierRepository.countByBranches_IdBranchAndIsActiveIsTrue(branchId);

        // quantidade de funcionarios alocados
        // TODO adicionar quantidade de funcionarios alocados
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

    private long getSafeLong(Object[] array, int index) {
        if (array != null && array.length > index && array[index] instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }
}
