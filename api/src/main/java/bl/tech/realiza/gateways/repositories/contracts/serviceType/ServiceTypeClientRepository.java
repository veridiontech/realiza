package bl.tech.realiza.gateways.repositories.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceTypeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceTypeClientRepository extends JpaRepository<ServiceTypeClient, String> {
    List<ServiceTypeClient> findAllByClient_IdClient(String idOwner);
}
