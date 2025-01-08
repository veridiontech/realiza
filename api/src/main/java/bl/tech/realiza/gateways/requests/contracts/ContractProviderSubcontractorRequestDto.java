package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.Requirement;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractProviderSubcontractorRequestDto {
    @NotEmpty
    private String id_contract;
    @NotEmpty
    private String service_type;
    @NotEmpty
    private String service_duration;
    @NotEmpty
    private String service_name;
    @NotEmpty
    private String description;
    @NotEmpty
    private String allocated_limit;
    @NotNull
    private Date start_date;
    @NotNull
    private Date end_date;
    @NotEmpty
    private String contract_reference;
    @NotEmpty
    private String providerSubcontractor;
    @NotEmpty
    private List<String> activities;
    @NotEmpty
    private List<String> requirements;
}
