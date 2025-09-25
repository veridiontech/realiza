package bl.tech.realiza.usecases.interfaces.auditLogs;

import bl.tech.realiza.gateways.requests.dashboard.history.DocumentHistoryRequest;
import bl.tech.realiza.gateways.responses.dashboard.history.DocumentStatusHistoryResponse;

import java.time.YearMonth;
import java.util.Map;

public interface DocumentStatusHistoryService {
    void save();
    Map<YearMonth, DocumentStatusHistoryResponse> findAllByDateAndId(String id, DocumentHistoryRequest request);
}
