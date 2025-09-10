//package bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix;
//
//import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
//import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSubgroupSnapshot;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Date;
//import java.util.Optional;
//
//public interface DocumentMatrixSubgroupSnapshotRepository extends JpaRepository<DocumentMatrixSubgroupSnapshot, String> {
//    Optional<DocumentMatrixSubgroupSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idDocumentSubgroup, Date from, SnapshotFrequencyEnum frequency);
//}
