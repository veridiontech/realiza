package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractAndSupplierCreateResponseDto {
    // contract
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String contractReference;
    private String description;
    private String allocatedLimit;
    private String responsible;
    private Contract.ExpenseType expenseType;
    private Date startDate;
    private Date endDate;
    private Boolean subcontractPermission;
    private String contractSupplierId;
    private Activity activity;
    private List<Requirement> requirements;

    // client
    private String branchName;
    private String idBranch;

    // supplier
    private String providerSupplierName;
    private String idProviderSupplier;
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String logoId;
    private byte[] logoData;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private List<ProviderResponseDto.BranchDto> branches;

    // subcontractor
    private String supplier;

    // supplier
    private String client;
}
