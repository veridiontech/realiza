package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.QueueLogService;
import bl.tech.realiza.services.queue.replication.ReplicationMessage;
import bl.tech.realiza.services.queue.replication.ReplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfig.SETUP_QUEUE,concurrency = "2-6")
public class SetupQueueConsumer {

    private final SetupService setupService;
    private final QueueLogService queueLogService;
    private final ReplicationService replicationService;

    @RabbitHandler
    public void consume(SetupMessage message) {
        long start = System.currentTimeMillis();
        try {
            switch (message.getType()) {
                case "NEW_CLIENT" -> {
                    setupService.setupNewClient(message.getClientId());
                    queueLogService.logSuccess("NEW_CLIENT", message.getClientId(), start);
                }
                case "NEW_CLIENT_PROFILES" -> {
                    setupService.setupNewClientProfiles(message.getClientId());
                    queueLogService.logSuccess("NEW_CLIENT_PROFILES", message.getClientId(), start);
                }
                case "NEW_BRANCH" -> {
                    setupService.setupBranch(message.getBranchId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_BRANCH", message.getBranchId(), start);
                }
                case "REPLICATE_BRANCH" -> {
                    setupService.setupReplicateBranch(message.getBranchId());
                    queueLogService.logSuccess("REPLICATE_BRANCH", message.getBranchId(), start);
                }
                case "NEW_CONTRACT_SUPPLIER" -> {
                    setupService.setupContractSupplier(message.getContractSupplierId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CONTRACT_SUPPLIER", message.getContractSupplierId(), start);
                }
                case "NEW_CONTRACT_SUBCONTRACTOR" -> {
                    setupService.setupContractSubcontractor(message.getContractSubcontractorId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CONTRACT_SUBCONTRACTOR", message.getContractSubcontractorId(), start);
                }
                case "EMPLOYEE_CONTRACT_SUPPLIER" -> {
                    setupService.setupEmployeeToContractSupplier(message.getContractSupplierId(), message.getEmployeeIds());
                    queueLogService.logSuccess("EMPLOYEE_CONTRACT_SUPPLIER", message.getContractSupplierId(), start);
                }
                case "EMPLOYEE_CONTRACT_SUBCONTRACT" -> {
                    setupService.setupEmployeeToContractSubcontract(message.getContractSubcontractorId(), message.getEmployeeIds());
                    queueLogService.logSuccess("EMPLOYEE_CONTRACT_SUBCONTRACT", message.getContractSubcontractorId(), start);
                }
                case "REMOVE_EMPLOYEE_CONTRACT" -> {
                    setupService.setupRemoveEmployeeFromContract(message.getContractId(), message.getEmployeeIds());
                    queueLogService.logSuccess("REMOVE_EMPLOYEE_CONTRACT", message.getContractId(), start);
                }
                default -> throw new IllegalArgumentException("Tipo inválido: " + message.getType());
            }
        } catch (Exception e) {
            queueLogService.logFailure(message.getType(), getId(message), e, start);
            throw e;
        }
    }

    @RabbitHandler
    public void consume(ReplicationMessage message) {
        long start = System.currentTimeMillis();
        try {
            switch (message.getType()) {
                case "REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM" -> {
                    replicationService.setupReplicateDocumentMatrixFromSystem(message.getDocumentId());
                    queueLogService.logSuccess("REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM", message.getDocumentId(), start);
                }
                case "CREATE_DOCUMENT_MATRIX" -> {
                    replicationService.setupCreateDocumentMatrixReplicateForBranches(message.getDocumentId());
                    queueLogService.logSuccess("CREATE_DOCUMENT_MATRIX", message.getDocumentId(), start);
                }
                case "CREATE_ACTIVITY" -> {
                    replicationService.replicateCreateActivity(message.getActivityId(), message.getBranchIds());
                    queueLogService.logSuccess("CREATE_ACTIVITY", message.getActivityId(), start);
                }
                case "UPDATE_ACTIVITY" -> {
                    replicationService.replicateUpdateActivity(message.getActivityId(), message.getTitle(), message.getActivityRisk(), message.getBranchIds());
                    queueLogService.logSuccess("UPDATE_ACTIVITY", message.getActivityId(), start);
                }
                case "DELETE_ACTIVITY" -> {
                    replicationService.replicateDeleteActivity(message.getTitle(), message.getBranchIds());
                    queueLogService.logSuccess("DELETE_ACTIVITY", message.getActivityId(), start);
                }
                case "CREATE_SERVICE_TYPE" -> {
                    replicationService.replicateCreateServiceType(message.getServiceTypeBranchId(), message.getBranchIds());
                    queueLogService.logSuccess("CREATE_ACTIVITY", message.getActivityId(), start);
                }
                case "UPDATE_SERVICE_TYPE" -> {
                    replicationService.replicateUpdateServiceType(message.getServiceTypeBranchId(), message.getTitle(), message.getServiceTypeRisk(), message.getBranchIds());
                    queueLogService.logSuccess("UPDATE_ACTIVITY", message.getActivityId(), start);
                }
                case "DELETE_SERVICE_TYPE" -> {
                    replicationService.replicateDeleteServiceType(message.getTitle(), message.getServiceTypeRisk(), message.getBranchIds());
                    queueLogService.logSuccess("DELETE_ACTIVITY", message.getActivityId(), start);
                }
                case "ALLOCATE_DOCUMENT_FROM_ACTIVITY" -> {
                    replicationService.replicateAllocateDocumentToActivity(message.getDocumentId(), message.getActivityId(), message.getBranchIds());
                    queueLogService.logSuccess("ALLOCATE_DOCUMENT_FROM_ACTIVITY", message.getDocumentId(), start);
                }
                case "DEALLOCATE_DOCUMENT_FROM_ACTIVITY" -> {
                    replicationService.replicateDeallocateDocumentToActivity(message.getDocumentId(), message.getActivityId(), message.getBranchIds());
                    queueLogService.logSuccess("DEALLOCATE_DOCUMENT_FROM_ACTIVITY", message.getDocumentId(), start);
                }
                case "ALLOCATE_DOCUMENT_FROM_BRANCH" -> {
                    replicationService.replicateAllocateDocumentToBranch(message.getDocumentId(), message.getTitle(), message.getBranchIds());
                    queueLogService.logSuccess("ALLOCATE_DOCUMENT_FROM_BRANCH", message.getDocumentId(), start);
                }
                case "DEALLOCATE_DOCUMENT_FROM_BRANCH" -> {
                    replicationService.replicateDeallocateDocumentToBranch(message.getDocumentId(), message.getTitle(), message.getBranchIds());
                    queueLogService.logSuccess("DEALLOCATE_DOCUMENT_FROM_BRANCH", message.getDocumentId(), start);
                }
                case "EXPIRATION_DATE_DOCUMENT_UPDATE" -> {
                    replicationService.replicateExpirationDateDocumentUpdate(message.getDocumentId(), message.getBranchIds());
                    queueLogService.logSuccess("EXPIRATION_DATE_DOCUMENT_UPDATE", message.getDocumentId(), start);
                }
                default -> throw new IllegalArgumentException("Tipo inválido: " + message.getType());
            }
        } catch (Exception e) {
            queueLogService.logFailure(message.getType(), getId(message), e, start);
            throw e;
        }
    }

    private String getId(SetupMessage msg) {
        return switch (msg.getType()) {
            case "NEW_CLIENT", "NEW_CLIENT_PROFILES" -> msg.getClientId();
            case "NEW_BRANCH", "REPLICATE_BRANCH" -> msg.getBranchId();
            case "NEW_CONTRACT_SUPPLIER", "EMPLOYEE_CONTRACT_SUPPLIER" -> msg.getContractSupplierId();
            case "NEW_CONTRACT_SUBCONTRACTOR", "EMPLOYEE_CONTRACT_SUBCONTRACT" -> msg.getContractSubcontractorId();
            case "REMOVE_EMPLOYEE_CONTRACT" -> msg.getContractId();
            default -> "SEM_ID";
        };
    }

    private String getId(ReplicationMessage msg) {
        return switch (msg.getType()) {
            case "REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM", "CREATE_DOCUMENT_MATRIX", "ALLOCATE_DOCUMENT_FROM_ACTIVITY",
                 "DEALLOCATE_DOCUMENT_FROM_ACTIVITY", "ALLOCATE_DOCUMENT_FROM_BRANCH",
                 "DEALLOCATE_DOCUMENT_FROM_BRANCH", "EXPIRATION_DATE_DOCUMENT_UPDATE" -> msg.getDocumentId();
            case "CREATE_ACTIVITY", "UPDATE_ACTIVITY", "DELETE_ACTIVITY" -> msg.getActivityId();
            case "CREATE_SERVICE_TYPE", "UPDATE_SERVICE_TYPE", "DELETE_SERVICE_TYPE" -> msg.getServiceTypeBranchId();
            default -> "SEM_ID";
        };
    }
}


