package bl.tech.realiza.gateways.repositories.services.snapshots.user;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.user.UserSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, String> {
    Optional<UserSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idUser, Date from, SnapshotFrequencyEnum frequency);
    Page<UserSnapshot> findAllById_SnapshotDateBeforeAndId_Frequency(Date from, SnapshotFrequencyEnum snapshotFrequencyEnum, Pageable pageable);
}
