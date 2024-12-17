package bl.tech.realiza.gateways.responses.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProviderSubcontractorResponseDto {
    private String id_provider;
    private String cnpj;
    private String supplier;
}
