package bl.tech.realiza.gateways.responses.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractSubcontractorResponseDto {
    private String idContract;
    private String serviceType;
    private String serviceName;
    private String description;
    private String responsible;
    private String cnpj;
    private String corporateName;
    private Contract.ExpenseType expenseType;
    private String contractReference;
    private Integer subcontractLevel;
    private String idContractSupplier;
    private Boolean finished;
    private Date dateStart;
    private Contract.IsActive isActive;
    private List<String> activities;
    private String idSupplier;
    private String nameSupplier;
    private String idSubcontractor;
    private String nameSubcontractor;
}
