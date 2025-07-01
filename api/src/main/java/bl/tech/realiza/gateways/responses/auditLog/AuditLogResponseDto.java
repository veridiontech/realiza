package bl.tech.realiza.gateways.responses.auditLog;

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
    private LocalDateTime createdAt;
    private String responsibleId;
    private String responsibleFullName;

    private String activityId;
    private String activityName;

    private String branchId;
    private String branchName;

    private String clientId;
    private String clientName;

    private String contractId;
    private String contractName;

    private String documentId;
    private String documentName;

    private String employeeId;
    private String employeeName;

    private String providerId;
    private String providerName;

    private String serviceTypeId;
    private String serviceTypeName;

    private String userId;
    private String userName;
}
