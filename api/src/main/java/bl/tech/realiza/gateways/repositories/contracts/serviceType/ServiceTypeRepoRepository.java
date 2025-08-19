package bl.tech.realiza.gateways.repositories.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepoRepository extends JpaRepository<ServiceTypeRepo, String> {
    Boolean existsByTitle(String name);
}
