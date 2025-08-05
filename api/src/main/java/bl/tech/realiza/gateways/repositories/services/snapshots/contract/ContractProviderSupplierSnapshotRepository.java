package bl.tech.realiza.gateways.repositories.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSupplierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractProviderSupplierSnapshotRepository extends JpaRepository<ContractProviderSupplierSnapshot, String> {
}
