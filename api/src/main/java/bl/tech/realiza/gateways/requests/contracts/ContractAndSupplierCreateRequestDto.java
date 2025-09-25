package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractAndSupplierCreateRequestDto {
    // contract
    private String serviceDuration;
    private String serviceName;
    private String contractReference;
    private String description;
    private String allocatedLimit;
    private String idResponsible;
    private Contract.ExpenseType expenseType;
    private Boolean labor;
    private Boolean hse;
    private Date startDate;
    private Date endDate;

    // contract supplier
    private Boolean subcontractPermission;
    private String idServiceType;
    private List<String> idActivityList;
    private String idBranch;

    // supplier
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
}
