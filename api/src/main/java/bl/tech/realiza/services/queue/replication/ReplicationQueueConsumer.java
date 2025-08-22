package bl.tech.realiza.services.queue.replication;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.QueueLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplicationQueueConsumer {

    private final ReplicationService replicationService;
    private final QueueLogService queueLogService;

    @RabbitListener(queues = RabbitConfig.REPLICATION_QUEUE)
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

    @RabbitListener(queues = RabbitConfig.REPLICATION_DLQ)
    public void handleDlq(ReplicationMessage message) {
        System.err.printf("🔁 Mensagem Replication movida para DLQ: %s - %s%n", message.getType(), getId(message));
    }

    public String getId(ReplicationMessage msg) {
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