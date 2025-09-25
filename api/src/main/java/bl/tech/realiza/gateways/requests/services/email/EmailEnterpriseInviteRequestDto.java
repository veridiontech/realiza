package bl.tech.realiza.gateways.requests.services.email;

import bl.tech.realiza.domains.providers.Provider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @NotNull
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
