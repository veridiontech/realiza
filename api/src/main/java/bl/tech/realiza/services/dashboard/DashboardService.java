package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final BranchRepository branchRepository;
    private final DocumentRepository documentRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final EmployeeRepository employeeRepository;

    public DashboardHomeResponseDto getHomeInfo(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        double adherence;
        Integer activeContractQuantity;
        Integer activeEmployeeQuantity;

        Object[] resultSupplier = documentProviderSupplierRepository
                .countTotalAndPendentesByBranch(branch.getIdBranch(), Document.Status.PENDENTE);

        Object[] resultEmployeeSupplier = documentEmployeeRepository
                .countTotalAndPendentesByContractSupplierBranch(branch.getIdBranch(), Document.Status.PENDENTE);

        Object[] resultSubcontractor = documentProviderSupplierRepository
                .countTotalAndPendentesByBranch(branch.getIdBranch(), Document.Status.PENDENTE);

        Object[] resultEmployeeSubcontractor = documentEmployeeRepository
                .countTotalAndPendentesByContractSubcontractorBranch(branch.getIdBranch(), Document.Status.PENDENTE);

        long total = ((Number) resultEmployeeSupplier[0]).longValue()
                + ((Number) resultEmployeeSubcontractor[0]).longValue()
                + ((Number) resultSubcontractor[0]).longValue()
                + ((Number) resultSupplier[0]).longValue();
        long pendentes = ((Number) resultEmployeeSupplier[1]).longValue()
                + ((Number) resultEmployeeSubcontractor[1]).longValue()
                + ((Number) resultSubcontractor[1]).longValue()
                + ((Number) resultSupplier[1]).longValue();

        adherence = total > 0 ? (pendentes * 100.0 / total) : 0;

        activeContractQuantity = contractProviderSupplierRepository.countByBranch_IdBranchAndFinishedIsFalse(branch.getIdBranch()).intValue();

        activeEmployeeQuantity = employeeRepository.countAllBySupplier_Branches_IdBranch(branch.getIdBranch()).intValue()
        + employeeRepository.countAllBySubcontract_ProviderSupplier_Branches_IdBranch(branch.getIdBranch()).intValue();

        return DashboardHomeResponseDto.builder()
                .adherence(adherence)
                .activeContractQuantity(activeContractQuantity)
                .activeEmployeeQuantity(activeEmployeeQuantity)
                .build();
    }

    public DashboardDetailsResponseDto getDetailsInfo(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<DashboardDetailsResponseDto.TypeStatus> documentStatus = new ArrayList<>();
        List<DashboardDetailsResponseDto.Exemption> documentExemption = new ArrayList<>();
        List<DashboardDetailsResponseDto.Pending> pendingRanking = new ArrayList<>();

        List<DashboardDetailsResponseDto.Status> status = new ArrayList<>();

        List<String> types = branch.getDocumentBranches().stream()
                .map(DocumentBranch::getType)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        /*types.forEach(type -> {
            DashboardDetailsResponseDto.TypeStatus.builder()
                    .name(type)
                    .status(status)
                    .build();
                    .quantity(
                            branch.getDocumentBranches().stream()
                                    .map(DocumentBranch::getType)
                                    .filter(type::equals)
                                    .toList()
                                    .size()
                    )

        })*/


        return DashboardDetailsResponseDto.builder()
                .documentStatus(documentStatus)
                .documentExemption(documentExemption)
                .pendingRanking(pendingRanking)
                .build();
    }
}
