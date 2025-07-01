package bl.tech.realiza.gateways.repositories.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.service.ServiceTypeControlPanelResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceTypeBranchRepository extends JpaRepository<ServiceTypeBranch, String> {
    List<ServiceTypeBranch> findAllByBranch_IdBranch(String idBranch);

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.clients.controlPanel.service.ServiceTypeControlPanelResponseDto(
        stb.idServiceType,
        stb.title,
        stb.risk
    )
    FROM ServiceTypeBranch stb
    WHERE stb.branch.idBranch = :branchId
""")
    List<ServiceTypeControlPanelResponseDto> findAllControlPanelActivityResponseDtoByBranch_IdBranch(
            @Param("branchId") String branchId);
}
