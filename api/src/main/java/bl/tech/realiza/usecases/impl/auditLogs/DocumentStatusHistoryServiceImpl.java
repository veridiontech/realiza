package bl.tech.realiza.usecases.impl.auditLogs;

import bl.tech.realiza.domains.auditLogs.dashboard.DocumentStatusHistory;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.repositories.auditLogs.dashboard.DocumentStatusHistoryRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.requests.dashboard.history.DocumentHistoryRequest;
import bl.tech.realiza.gateways.responses.dashboard.history.DocumentStatusHistoryResponse;
import bl.tech.realiza.services.dashboard.DashboardDocumentSpecification;
import bl.tech.realiza.services.dashboard.DashboardProviderDocumentHistorySpecification;
import bl.tech.realiza.usecases.interfaces.auditLogs.DocumentStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentStatusHistoryServiceImpl implements DocumentStatusHistoryService {
    private final ProviderRepository providerRepository;
    private final DocumentRepository documentRepository;
    private final DocumentStatusHistoryRepository documentStatusHistoryRepository;

    @Override
    public void save() {
        List<String> findAllProviderIds = providerRepository.findAllActiveIds();
        YearMonth previousMonth = YearMonth.now(ZoneId.of("America/Sao_Paulo")).minusMonths(1);
        List<String> documentTypes = documentRepository.findDistinctDocumentType();
        for (String id : findAllProviderIds) {
            Provider provider = providerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Provider not found"));
            List<String> providerIds = new ArrayList<>();
            providerIds.add(provider.getIdProvider());
            Specification<Document> spec = Specification.where(null);
            spec = spec.and(DashboardDocumentSpecification.byProviderIds(providerIds));
            for (String type : documentTypes) {
                List<String> singleDocumentType = new ArrayList<>();
                singleDocumentType.add(type);
                long totalDocuments = documentRepository.count(spec
                        .and(DashboardDocumentSpecification.byDocumentTypes(singleDocumentType)));
                long adherentDocuments = documentRepository.count(spec
                        .and(DashboardDocumentSpecification.byAdherenceIsTrue())
                        .and(DashboardDocumentSpecification.byDocumentTypes(singleDocumentType)));
                long conformityDocuments = documentRepository.count(spec
                        .and(DashboardDocumentSpecification.byConformingIsTrue())
                        .and(DashboardDocumentSpecification.byDocumentTypes(singleDocumentType)));
                documentStatusHistoryRepository.save(DocumentStatusHistory.builder()
                                .totalDocuments(totalDocuments)
                                .adherent(adherentDocuments)
                                .conformity(conformityDocuments)
                                .provider(provider)
                                .historyPeriod(previousMonth)
                                .documentType(type)
                        .build());
            }
        }
    }

    @Override
    public Map<YearMonth, DocumentStatusHistoryResponse> findAllByDateAndId(String id, DocumentHistoryRequest request) {
        YearMonth startYearMonth = YearMonth.of(request.getStartYear().getValue(), request.getStartMonth());
        YearMonth endYearMonth = YearMonth.of(request.getEndYear().getValue(), request.getEndMonth());

        Specification<DocumentStatusHistory> spec = Specification.where(null);
        if (request.getBranchIds() != null && !request.getBranchIds().isEmpty()) {
            spec.and(DashboardProviderDocumentHistorySpecification.byBranchIds(request.getBranchIds()));
        } else {
            spec.and(DashboardProviderDocumentHistorySpecification.byClientId(id));
        }
        if (request.getProviderIds() != null && !request.getProviderIds().isEmpty()) {
            spec.and(DashboardProviderDocumentHistorySpecification.byProviderIds(request.getProviderIds()));
        }
        if (request.getProviderCnpjs() != null && !request.getProviderCnpjs().isEmpty()) {
            spec.and(DashboardProviderDocumentHistorySpecification.byProviderCnpjs(request.getProviderCnpjs()));
        }
        if (request.getDocumentTypes() != null && !request.getDocumentTypes().isEmpty()) {
            spec.and(DashboardProviderDocumentHistorySpecification.byDocumentTypes(request.getProviderCnpjs()));
        }
        spec.and(DashboardProviderDocumentHistorySpecification.byHistoryPeriodBetween(startYearMonth, endYearMonth));
        List<DocumentStatusHistory> histories = documentStatusHistoryRepository
                .findAll(spec);

        return histories.stream()
                .collect(Collectors.toMap(
                        DocumentStatusHistory::getHistoryPeriod,
                        this::toResponse));
    }

    private DocumentStatusHistoryResponse toResponse(DocumentStatusHistory entity) {
        return DocumentStatusHistoryResponse.builder()
                .id(entity.getId())
                .total(entity.getTotalDocuments())
                .adherent(entity.getAdherent())
                .conformity(entity.getConformity())
                .build();
    }
}
