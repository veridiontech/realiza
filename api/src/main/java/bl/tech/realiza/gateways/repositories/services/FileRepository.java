package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.services.FileDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileDocument, String> {
    Page<FileDocument> findAllByCanBeOverwritten(Boolean canBeOverwritten, Pageable pageable);
}
