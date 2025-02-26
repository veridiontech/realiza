package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractAndSupplierCreateRequestDto {
    // contract
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String allocatedLimit;
    private String responsible;
    private Contract.ExpenseType expenseType;
    private Date startDate;
    private Date endDate;
    private String contractReference;
    private Boolean subcontractPermission;
    private String supplierContractId;
    private List<String> activities;
    private List<String> requirements;
    private String cnpj;
    private Document.Risk risk;
    private String branch;
    // supplier
    private String tradeName;
    private String corporateName;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
}
