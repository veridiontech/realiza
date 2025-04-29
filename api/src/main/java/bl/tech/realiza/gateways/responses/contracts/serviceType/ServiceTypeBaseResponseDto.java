package bl.tech.realiza.gateways.responses.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class ServiceTypeBaseResponseDto implements ServiceTypeResponseDto {
    private String idServiceType;
    private String title;
    private ServiceType.Risk risk;
    private LocalDateTime creationDate;
}
