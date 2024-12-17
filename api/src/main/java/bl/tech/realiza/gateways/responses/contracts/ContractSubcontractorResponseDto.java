package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contracts.Activity;
import bl.tech.realiza.domains.contracts.Requirement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractSubcontractorResponseDto {
    private String id_contract;
    private String service_type;
    private String service_duration;
    private String service_name;
    private String description;
    private String allocated_limit;
    private Date start_date;
    private Date end_date;
    private String contract_reference;
    private String providerSubcontractor;
    private List<Activity> activities;
    private List<Requirement> requirements;
}
