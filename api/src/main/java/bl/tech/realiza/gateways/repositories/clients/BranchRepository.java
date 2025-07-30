package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.gateways.responses.clients.branches.BranchNameResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Page<Branch> findAllByClient_IdClientAndIsActiveIsTrue(String client, Pageable pageable);
    List<Branch> findAllByClient_IdClientAndIsActiveIsTrue(String client);
    Page<Branch> findAllByIsActiveIsTrue(Pageable pageable);
    Page<Branch> findAllByCenter_IdCenter(String idCenter, Pageable pageable);
    Branch findFirstByClient_IdClientOrderByCreationDateAsc(String idClient);

    Collection<Branch> findAllByIsActive(boolean b);

    Branch findFirstByClient_IdClientAndIsActiveIsTrueAndBaseIsTrueOrderByCreationDate(String idClient);

    @Query("""
    SELECT b.idBranch
    FROM Branch b
    WHERE b.client.idClient = :clientId
""")
    List<String> findAllBranchIdsByClientId(@Param("clientId") String clientId);

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.clients.branches.BranchNameResponseDto(
    b.idBranch,
    b.name
    )
    FROM Branch b
    WHERE b.idBranch in :branchIds
""")
    List<BranchNameResponseDto> findAllNameByAccess(@Param("branchIds") List<String> branchIds);
}
