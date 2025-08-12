package bl.tech.realiza.gateways.repositories.services.snapshots.employees;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeSnapshotRepository extends JpaRepository<EmployeeSnapshot, String> {
    List<EmployeeSnapshot> findAllById_IdInAndId_SnapshotDateAndId_Frequency(List<String> idList, Date date, SnapshotFrequencyEnum frequency);
    Optional<EmployeeSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idEmployee, Date from, SnapshotFrequencyEnum frequency);
    Page<EmployeeSnapshot> findAllById_SnapshotDateBeforeAndId_Frequency(Date from, SnapshotFrequencyEnum snapshotFrequencyEnum, Pageable pageable);

    @Query("""
    SELECT COUNT(e)
    FROM EmployeeSnapshot e
    JOIN e.contracts c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE cps.branch.client.id.id = :clientId
        AND e.situation = :situation
        AND e.id.snapshotDate = :date
        AND e.id.frequency = :frequency
""")
    Long countEmployeeSupplierByClientIdAndSituationAndDateAndFrequency(@Param("clientId") String clientId,
                                                                        @Param("situation") Employee.Situation situation,
                                                                        @Param("date") Date date,
                                                                        @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(e)
    FROM EmployeeSnapshot e
    JOIN e.contracts c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE cpsb.contractSupplier.branch.client.id = :clientId
        AND e.situation = :situation
        AND e.id.snapshotDate = :date
        AND e.id.frequency = :frequency
""")
    Long countEmployeeSubcontractorByClientIdAndSituationAndDateAndFrequency(@Param("clientId") String clientId,
                                                                             @Param("situation") Employee.Situation situation,
                                                                             @Param("date") Date date,
                                                                             @Param("frequency") SnapshotFrequencyEnum frequency);
}
