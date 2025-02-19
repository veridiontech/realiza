package bl.tech.realiza.gateways.responses.services;

import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemManagementResponseDto {
    private String idUpdateDataRequest;
    private String title;
    private String details;
    private LocalDateTime creationDate;
    private UserResponseDto requester;
    private UserResponseDto newUser;
}
