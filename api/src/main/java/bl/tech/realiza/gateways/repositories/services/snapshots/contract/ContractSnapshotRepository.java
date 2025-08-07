package bl.tech.realiza.gateways.repositories.services.snapshots.contract;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface ContractSnapshotRepository extends JpaRepository<ContractSnapshot, String> {
    Optional<ContractSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idContract, Date from, SnapshotFrequencyEnum frequency);
}
