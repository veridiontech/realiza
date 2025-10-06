package bl.tech.realiza.gateways.requests.contracts.activity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@Builder
@Jacksonized
public class DocumentsToActivityRequest {
    @NotEmpty
    private List<String> documentBranchIds;
    private Boolean replicate;
    private List<String> branchIds;
}
