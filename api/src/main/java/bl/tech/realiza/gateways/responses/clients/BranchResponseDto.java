package bl.tech.realiza.gateways.responses.clients;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BranchResponseDto {
    private String idBranch;
    private String name;
    private String cnpj;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private String client;
    private List<DocumentMatrix> documents;
}
