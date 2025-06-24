package bl.tech.realiza.gateways.repositories.documents.employee;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentEmployeeRepository extends JpaRepository<DocumentEmployee, String> {
    Page<DocumentEmployee> findAllByEmployee_IdEmployee(String idSearch, Pageable pageable);
    List<DocumentEmployee> findAllByEmployee_IdEmployee(String idSearch);
    List<DocumentEmployee> findAllByRequest(Document.Request request);
    Long countByEmployee_Branch_IdBranchAndStatus(String branchId, Document.Status status);
    Long countByEmployee_Supplier_IdProviderAndStatus(String supplierId, Document.Status status);
    Long countByEmployee_Subcontract_IdProviderAndStatus(String subcontractorId, Document.Status status);

    @Query("""
    SELECT
            SUM(CASE WHEN de.status != :status THEN 1 ELSE 0 END) AS total,
            SUM(CASE WHEN de.status = :status THEN 1 ELSE 0 END) AS pendentes
    FROM DocumentEmployee de
    JOIN de.employee e
    JOIN e.contracts c
    JOIN ContractProviderSupplier cps ON cps.idContract = c.idContract
    WHERE cps.finished = false
        AND e.situation = 0
        AND cps.branch.idBranch = :branchId
""")
    Object[] countTotalAndPendentesByContractSupplierBranch(
            @Param("branchId") String branchId,
            @Param("status") Document.Status status
    );

    @Query("""
    SELECT
        de.type, de.status, COUNT(de)
    FROM DocumentEmployee de
    JOIN de.employee e
    JOIN e.contracts c
    JOIN ContractProviderSupplier cps ON cps.idContract = c.idContract
    WHERE cps.finished = false
        AND e.situation = 0
        AND cps.branch.idBranch = :branchId
    GROUP BY de.type, de.status
""")
    List<Object[]> countTotalTypesByBranch(@Param("branchId") String branchId);

    @Query("""
    SELECT
            COUNT(de) AS total,
            SUM(CASE WHEN de.status = :status THEN 1 ELSE 0 END) AS pendentes
    FROM DocumentEmployee de
    JOIN de.employee e
    JOIN e.contracts c
    JOIN ContractProviderSubcontractor cpsub ON cpsub.idContract = c.idContract
    JOIN cpsub.contractProviderSupplier cpsup
    WHERE cpsub.finished = false
        AND e.situation = 0
        AND cpsup.branch.idBranch = :branchId
""")
    Object[] countTotalAndPendentesByContractSubcontractorBranch(
            @Param("branchId") String branchId,
            @Param("status") Document.Status status
    );

    @Query("""
    SELECT COUNT(d)
    FROM DocumentEmployee d
    JOIN d.employee e
    WHERE e.branch.idBranch = :branchId AND d.status = :status
""")
    Long countByBranchIdAndStatus(@Param("branchId") String branchId, @Param("status") Document.Status status);

    @Query("""
    SELECT COUNT(d)
    FROM DocumentEmployee d
    JOIN d.employee e
    JOIN e.contracts c
    WHERE TYPE(c) = ContractProviderSupplier
      AND TREAT(c AS ContractProviderSupplier).branch.idBranch = :branchId
""")
    Long countByBranchId(@Param("branchId") String branchId);



}
