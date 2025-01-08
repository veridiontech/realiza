package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.UserProviderSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderSupplierRepository extends JpaRepository<UserProviderSupplier, String> {
}
