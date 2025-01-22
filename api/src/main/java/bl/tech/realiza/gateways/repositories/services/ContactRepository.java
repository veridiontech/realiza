package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.clients.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, String> {
    Page<Contact> findAllByClient_IdClient(String client, Pageable pageable);
    Page<Contact> findAllBySupplier_IdProvider(String supplier, Pageable pageable);
    Page<Contact> findAllBySubcontractor_IdProvider(String subcontractor, Pageable pageable);
}
