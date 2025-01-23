package bl.tech.realiza.gateways.responses.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProviderResponseDto {
    // provider
    private String idProvider;
    private String cnpj;
    private String companyName;
    private String tradeName;
    private String fantasyName;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private List<BranchDto> branches;

    // subcontractor
    private String supplier;

    // supplier
    private String client;

    @Data
    @Builder
    public static class BranchDto {
        private String idBranch;
        private String nameBranch;
    }
}
