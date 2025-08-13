package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.enums.RiskEnum;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.activity.ActivityControlPanelResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, String > {
    List<Activity> findAllByBranch_IdBranch(String idBranch);
    List<Activity> findAllByBranch_IdBranchAndTitle(String idBranch, String title);
    List<Activity> findAllByBranch_IdBranchAndTitleAndRisk(String idBranch, String title, RiskEnum risk);
    Optional<Activity> findByBranch_IdBranchAndActivityRepo_IdActivity(String idBranch, String idActivity);

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

    @Query("""
    SELECT b.idBranch
    FROM Activity a
    JOIN a.branch b
    WHERE a.idActivity = :activityId
""")
    String findBranchIdByActivity(
            @Param("activityId") String id);
}
