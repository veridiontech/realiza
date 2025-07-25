package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.services.ItemManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemManagementRepository extends JpaRepository<ItemManagement, String> {

    Page<ItemManagement> findAllByNewUserIsNotNull(Pageable pageable);
    Page<ItemManagement> findAllByNewProviderIsNotNull(Pageable pageable);
    Page<ItemManagement> findAllByContractDocumentIsNotNull(Pageable pageable);
    Page<ItemManagement> findAllByContractIsNotNull(Pageable pageable);
}
