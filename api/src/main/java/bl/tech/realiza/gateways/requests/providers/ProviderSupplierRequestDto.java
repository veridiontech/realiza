package bl.tech.realiza.gateways.requests.providers;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProviderSupplierRequestDto {
    private String id_provider;
    @NotEmpty
    private String cnpj;
    @NotEmpty
    private String client;
}
