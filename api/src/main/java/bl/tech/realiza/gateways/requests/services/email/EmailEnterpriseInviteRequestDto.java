package bl.tech.realiza.gateways.requests.services.email;

import bl.tech.realiza.domains.providers.Provider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class EmailEnterpriseInviteRequestDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String companyName;
    @NotEmpty
    private String requesterName;
    @NotEmpty
    private String serviceName;
    @NotEmpty
    private Date startDate;
    @NotEmpty
    private String requesterBranchName;
    @NotEmpty
    private String responsibleName;
    @NotEmpty
    private String contractReference;
    @NotEmpty
    private String idCompany; // id do slot da empresa
    private String idBranch;
    private String idSupplier;
}
