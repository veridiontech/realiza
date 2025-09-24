package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.employees.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Collection<Employee> findAllByDeleteRequest(boolean b);
    Long countAllByBranch_IdBranch(String branchId);
    Long countAllBySupplier_IdProvider(String supplierId);
    Long countAllBySubcontract_IdProvider(String subcontractId);
    Long countAllBySubcontract_ProviderSupplier_Branches_IdBranch(String branchId);
    Long countAllBySubcontract_ProviderSupplier_Branches_IdBranchAndSituation(String branchId, Employee.Situation situation);
    Long countAllBySupplier_Branches_IdBranch(String branchId);
    Long countAllBySupplier_Branches_IdBranchAndSituation(String branchId, Employee.Situation situation);

    @Query("""
    SELECT COUNT(e)
    FROM Employee e
    JOIN e.supplier ps
    JOIN ps.branches b
    WHERE b.idBranch = :branchId
    AND e.situation = :situation
""")
    int countAllByBranch_IdBranchAndSituation(
            @Param("branchId") String branchId,
            @Param("situation") Employee.Situation situation
    );

    @Query("""
    SELECT COUNT(e)
    FROM Employee e
    JOIN e.contractEmployees ce
    JOIN ce.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE cps.branch.client.idClient = :clientId
        AND e.situation = :situation
""")
    Long countEmployeeSupplierByClientIdAndAllocated(@Param("clientId") String clientId,
                                                     @Param("situation") Employee.Situation situation);

    @Query("""
    SELECT COUNT(e)
    FROM Employee e
    JOIN e.contractEmployees ce
    JOIN ce.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor ) cpsb
    WHERE cpsb.contractProviderSupplier.branch.client.idClient = :clientId
        AND e.situation = :situation
""")
    Long countEmployeeSubcontractorByClientIdAndAllocated(@Param("clientId") String clientId,
                                                     @Param("situation") Employee.Situation situation);

}