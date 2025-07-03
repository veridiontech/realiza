package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.activity.ActivityControlPanelResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, String > {
    Collection<Activity> findAllByDeleteRequest(boolean b);
    List<Activity> findAllByBranch_IdBranch(String idBranch);

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.clients.controlPanel.activity.ActivityControlPanelResponseDto(
        a.idActivity,
        a.title,
        a.risk
    )
    FROM Activity a
    WHERE a.branch.idBranch = :branchId
""")
    List<ActivityControlPanelResponseDto> findAllControlPanelActivityResponseDtoByBranch_IdBranch(
            @Param("branchId") String id);

    List<Activity> findAllByBranch_Client_IdClientAndTitle(String idClient, String title);

    List<Activity> findAllByBranch_Client_IdClientAndTitleAndRisk(String idClient, String title, Activity.Risk risk);
}
