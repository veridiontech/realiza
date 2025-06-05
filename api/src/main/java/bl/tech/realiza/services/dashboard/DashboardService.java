package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static bl.tech.realiza.domains.documents.Document.Status.APROVADO;

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

    public DashboardHomeResponseDto getHomeInfo(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        double adherence;
        int activeContractQuantity;
        int activeEmployeeQuantity;

        Object[] resultEmployeeSupplierRaw = documentEmployeeRepository
                .countTotalAndPendentesByContractSupplierBranch(branch.getIdBranch(), Document.Status.PENDENTE);
        Object[] resultEmployeeSupplier = (Object[]) resultEmployeeSupplierRaw[0];

        Object[] resultEmployeeSubcontractorRaw = documentEmployeeRepository
                .countTotalAndPendentesByContractSubcontractorBranch(branch.getIdBranch(), Document.Status.PENDENTE);
        Object[] resultEmployeeSubcontractor = (Object[]) resultEmployeeSubcontractorRaw[0];

        Object[] resultSubcontractorRaw = documentProviderSubcontractorRepository
                .countTotalAndPendentesByBranch(branch.getIdBranch(), Document.Status.PENDENTE);
        Object[] resultSubcontractor = (Object[]) resultSubcontractorRaw[0];

        Object[] resultSupplierRaw = documentProviderSupplierRepository
                .countTotalAndPendentesByBranch(branch.getIdBranch(), Document.Status.PENDENTE);
        Object[] resultSupplier = (Object[]) resultSupplierRaw[0];


        long total = getSafeLong(resultEmployeeSupplier, 0)
                + getSafeLong(resultEmployeeSubcontractor, 0)
                + getSafeLong(resultSubcontractor, 0)
                + getSafeLong(resultSupplier, 0);

        long pendentes = getSafeLong(resultEmployeeSupplier, 1)
                + getSafeLong(resultEmployeeSubcontractor, 1)
                + getSafeLong(resultSubcontractor, 1)
                + getSafeLong(resultSupplier, 1);


        adherence = total > 0 ? ((total - pendentes) * 100.0 / total) : 0;

        activeContractQuantity = contractProviderSupplierRepository.countByBranch_IdBranchAndFinishedIsFalse(branch.getIdBranch()).intValue();

        activeEmployeeQuantity = employeeRepository.countAllBySupplier_Branches_IdBranchAndSituation(branch.getIdBranch(), Employee.Situation.ALOCADO).intValue()
        + employeeRepository.countAllBySupplier_Branches_IdBranchAndSituation(branch.getIdBranch(), Employee.Situation.DESALOCADO).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(branch.getIdBranch(), Employee.Situation.ALOCADO).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(branch.getIdBranch(), Employee.Situation.DESALOCADO).intValue();

        return DashboardHomeResponseDto.builder()
                .adherence(adherence)
                .activeContractQuantity(activeContractQuantity)
                .activeEmployeeQuantity(activeEmployeeQuantity)
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
            Document.Status status = (Document.Status) row[1];
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
                    .countTotalAndPendentesByBranch(b.getIdBranch(), Document.Status.PENDENTE);
            Object[] supplier = (Object[]) supplierRaw[0];

            Object[] employeeSupplierRaw = documentEmployeeRepository
                    .countTotalAndPendentesByContractSupplierBranch(b.getIdBranch(), Document.Status.PENDENTE);
            Object[] employeeSupplier = (Object[]) employeeSupplierRaw[0];

            Object[] subcontractorRaw = documentProviderSupplierRepository
                    .countTotalAndPendentesByBranch(b.getIdBranch(), Document.Status.PENDENTE);
            Object[] subcontractor = (Object[]) subcontractorRaw[0];

            Object[] employeeSubcontractorRaw = documentEmployeeRepository
                    .countTotalAndPendentesByContractSubcontractorBranch(b.getIdBranch(), Document.Status.PENDENTE);
            Object[] employeeSubcontractor = (Object[]) employeeSubcontractorRaw[0];


            long supp = getSafeLong(supplier, 0);
            long total = getSafeLong(supplier, 0)
                    + getSafeLong(employeeSupplier, 0)
                    + getSafeLong(subcontractor, 0)
                    + getSafeLong(employeeSubcontractor, 0);

            long pendentes = getSafeLong(supplier, 1)
                    + getSafeLong(employeeSupplier, 1)
                    + getSafeLong(subcontractor, 1)
                    + getSafeLong(employeeSubcontractor, 1);

            double adherence = total > 0 ? (pendentes * 100.0 / total) : 100;
            adherence = Math.round(adherence * 100.0) / 100.0;

            long docEmployeeApproved = documentEmployeeRepository.countByBranchIdAndStatus(branchId, APROVADO);
            // Agora calcular conformidade
            long aprovados = documentEmployeeRepository.countByBranchIdAndStatus(branchId, APROVADO)
                    + documentProviderSupplierRepository.countByBranchIdAndStatus(branchId, APROVADO)
                    + documentProviderSubcontractorRepository.countByBranchIdAndStatus(branchId, APROVADO);

            long docEmployeeValid = documentEmployeeRepository.countByBranchId(branchId);
            long totalValidos = documentEmployeeRepository.countByBranchId(branchId)
                    + documentProviderSupplierRepository.countByBranchId(branchId)
                    + documentProviderSubcontractorRepository.countByBranchId(branchId);

            double conformity = totalValidos > 0 ? (aprovados * 100.0 / totalValidos) : 0;
            int nonConforming = (int) (totalValidos - aprovados);

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
