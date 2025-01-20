package bl.tech.realiza.gateways.repositories.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMatrixRepository extends JpaRepository<DocumentMatrix, String> {
    Page<DocumentMatrix> findAllBySubGroup_IdDocumentSubgroup(String idSearch, Pageable pageable);
    Page<DocumentMatrix> findAllBySubGroup_Group_IdDocumentGroup(String idSearch, Pageable pageable);
}
