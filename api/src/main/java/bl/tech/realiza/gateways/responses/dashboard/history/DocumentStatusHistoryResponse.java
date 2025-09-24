package bl.tech.realiza.gateways.responses.dashboard.history;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class DocumentStatusHistoryResponse {
    private String id;
    private Long total;
    private Long adherent;
    private Long conformity;
}
