package bl.tech.realiza.gateways.requests.providers;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProviderSupplierRequestDto {
    private String idProvider;
    private String cnpj;
    private String client;
    private Boolean isActive;
}
