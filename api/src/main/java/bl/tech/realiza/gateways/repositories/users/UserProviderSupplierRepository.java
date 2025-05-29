package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.Notification;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProviderSupplierRepository extends JpaRepository<UserProviderSupplier, String> {
    Page<UserProviderSupplier> findAllByProviderSupplier_IdProviderAndRole(String idSearch, User.Role role, Pageable pageable);
    Page<UserProviderSupplier> findAllByProviderSupplier_IdProvider(String idProvider, Pageable pageable);
}
