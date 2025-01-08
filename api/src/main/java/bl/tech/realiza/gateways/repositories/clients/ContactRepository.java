package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, String> {
}
