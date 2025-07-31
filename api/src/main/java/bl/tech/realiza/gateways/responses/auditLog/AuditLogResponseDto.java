package bl.tech.realiza.gateways.responses.auditLog;

import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.OwnerEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuditLogResponseDto {
    private String id;
    private String description;
    private String notes;
    private AuditLogActionsEnum action;
    private LocalDateTime createdAt;
    private String userResponsibleId;
    private String userResponsibleCpf;
    private String userResponsibleFullName;
    private String userResponsibleEmail;

    private String activityId;
    private String activityTitle;

    private String branchId;
    private String branchName;

    private String clientId;
    private String clientCorporateName;

    private String contractId;
    private String contractReference;
    private String responsibleId;
    private String responsibleFullName;
    private String supplierId;
    private String supplierCorporateName;
    private String subcontractorId;
    private String subcontractorCorporateName;

    private String documentId;
    private String documentTitle;
    private String ownerId;
    private OwnerEnum owner;
    private String fileId;
    private Boolean hasDoc;

    private String employeeId;
    private String employeeName;
    private String enterpriseId;
    private String enterpriseCorporateName;

    private String providerId;
    private String providerCorporateName;

    private String serviceTypeId;
    private String serviceTypeTitle;

    private String userId;
    private String userCpf;
    private String userFullName;
    private String userEmail;
}
