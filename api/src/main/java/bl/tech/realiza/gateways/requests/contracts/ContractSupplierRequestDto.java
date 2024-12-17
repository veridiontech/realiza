package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contracts.Activity;
import bl.tech.realiza.domains.contracts.Requirement;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractSupplierRequestDto {
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
    private String providerSupplier;
    @NotEmpty
    private List<Activity> activities;
    @NotEmpty
    private List<Requirement> requirements;
}
