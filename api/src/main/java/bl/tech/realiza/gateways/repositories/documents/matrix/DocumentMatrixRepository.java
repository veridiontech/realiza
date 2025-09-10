package bl.tech.realiza.gateways.repositories.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentMatrixRepository extends JpaRepository<DocumentMatrix, String> {
    Page<DocumentMatrix> findAllByGroup_IdDocumentGroup(String idSearch, Pageable pageable);
    List<DocumentMatrix> findAllByGroup_GroupName(String nameSearch);

    @Query("""
    SELECT dm.name
    FROM DocumentMatrix dm
""")
    List<String> findAllTitles();
}
