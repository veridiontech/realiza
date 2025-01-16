package bl.tech.realiza.gateways.repositories.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMatrixRepository extends JpaRepository<DocumentMatrix, String> {
}
