package bl.tech.realiza.gateways.requests.providers;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProviderSubcontractorRequestDto {
    private String idProvider;
    private String cnpj;
    private String supplier;
    private Boolean isActive;
}
