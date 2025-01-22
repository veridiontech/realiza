package bl.tech.realiza.gateways.repositories.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMatrixSubgroupRepository extends JpaRepository<DocumentMatrixSubgroup, String> {
    Page<DocumentMatrixSubgroup> findAllByGroup_IdDocumentGroup(String idSearch, Pageable pageable);
}
