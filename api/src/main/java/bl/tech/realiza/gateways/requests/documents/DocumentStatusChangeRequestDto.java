package bl.tech.realiza.gateways.requests.documents;

import bl.tech.realiza.domains.documents.Document.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentStatusChangeRequestDto {
    @NotNull
    private Status status;
    @Size(max = 1000, message = "O motivo da reprovação não pode exceder 1000 caracteres.")
    private String notes;
}
