package bl.tech.realiza.usecases.impl.documents.document;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CrudDocumentImpl implements CrudDocument {

    private final DocumentRepository documentRepository;
    private final CrudNotification crudNotification;

    @Override
    public void expirationChange() {
        int page = 0;
        int size = 50;
        boolean hasNext;

        Page<Document> documentPage;
        do {
            documentPage = documentRepository.findAllByStatus(
                    Document.Status.APROVADO, PageRequest.of(page, size)
            );

            documentPage.forEach(document -> {
                if (document.getExpirationDate() != null &&
                        document.getExpirationDate().isBefore(LocalDate.now().atStartOfDay())) {
                    document.setStatus(Document.Status.VENCIDO);
                    documentRepository.save(document);
                }
            });

            hasNext = documentPage.hasNext();
            page++;
        } while (hasNext);
    }


    @Override
    public void expirationCheck() {
        int page = 0;
        int size = 50;
        boolean hasNext;

        Page<Document> documentPage;
        do {
            documentPage = documentRepository.findAllByStatus(
                    Document.Status.VENCIDO, PageRequest.of(page, size)
            );

            documentPage.forEach(document -> {
                if (document instanceof DocumentProviderSupplier providerSupplierDoc) {
                    crudNotification.saveExpiredSupplierDocumentNotificationForSupplierUsers(providerSupplierDoc);
                } else if (document instanceof DocumentProviderSubcontractor providerSubcontractorDoc) {
                    crudNotification.saveExpiredSubcontractDocumentNotificationForSubcontractorUsers(providerSubcontractorDoc);
                } else if (document instanceof DocumentEmployee employeeDoc) {
                    crudNotification.saveExpiredEmployeeDocumentNotificationForManagerUsers(employeeDoc);
                }
            });

            hasNext = documentPage.hasNext();
            page++;
        } while (hasNext);
    }
}
