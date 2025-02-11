package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentBranchRepository extends JpaRepository<DocumentBranch, String> {
    Page<DocumentBranch> findAllByBranch_IdBranch(String idSearch, Pageable pageable);
    List<DocumentBranch> findAllByBranch_IdBranch(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
    List<DocumentBranch> findAllByBranch_IdBranchAndLowLessThan8hIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndLowLessThan1mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndLowLessThan6mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndLowMoreThan6mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndMediumLessThan1mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndMediumLessThan6mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndMediumMoreThan6mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndHighLessThan1mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndHighLessThan6mIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndHighMoreThan6mIsTrue(String idSearch);

}
