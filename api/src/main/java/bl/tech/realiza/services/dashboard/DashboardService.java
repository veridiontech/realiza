package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final BranchRepository branchRepository;

    public DashboardHomeResponseDto getHomeInfo(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        Double adherence = null;
        Integer activeContractQuantity = null;
        Integer activeEmployeeQuantity = null;

        // logica para encontrar esses valores

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
