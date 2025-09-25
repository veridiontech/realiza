package bl.tech.realiza.gateways.responses.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentIAValidationResponse {
    private String documentType;
    private String reason;
    private boolean autoValidate;
    @JsonProperty("isValid")
    private boolean valid;
    private LocalDateTime documentDate;
}
