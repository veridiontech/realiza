package bl.tech.realiza.gateways.repositories.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, String> {
}
