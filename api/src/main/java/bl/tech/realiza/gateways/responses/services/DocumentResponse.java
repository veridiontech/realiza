package bl.tech.realiza.gateways.responses.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private String documentType;
    private boolean autoValidate;
    @JsonProperty("isValid")  // Mapeia "isValid" para "valid"
    private boolean valid;
}
