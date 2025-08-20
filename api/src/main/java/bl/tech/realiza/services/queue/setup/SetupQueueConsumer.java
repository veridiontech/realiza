package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.QueueLogService;
import bl.tech.realiza.services.queue.replication.ReplicationMessage;
import bl.tech.realiza.services.queue.replication.ReplicationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfig.SETUP_QUEUE,concurrency = "2-6")
public class SetupQueueConsumer {

    private final SetupService setupService;
    private final QueueLogService queueLogService;
    private final ReplicationService replicationService;

    @RabbitHandler
    public void consume(SetupMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, IOException { // 👈 MUDANÇA
        log.info("Consume SetupMessage");
        long start = System.currentTimeMillis();
        try {
            switch (message.getType()) {
                case "NEW_CLIENT" -> {
                    log.info("New client consumed");
                    setupService.setupNewClient(message.getClientId(), message.getProfilesFromRepo(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CLIENT", getId(message), start);
                }
                case "NEW_CLIENT_PROFILES" -> {
                    log.info("New client profile consumed");
                    setupService.setupNewClientProfiles(message.getClientId());
                    queueLogService.logSuccess("NEW_CLIENT_PROFILES", getId(message), start);
                }
                case "NEW_BRANCH" -> {
                    log.info("New branch consumed");
                    setupService.setupBranch(message.getBranchId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_BRANCH", getId(message), start);
                }
                case "REPLICATE_BRANCH" -> {
                    log.info("New replicate branch consumed");
                    setupService.setupReplicateBranch(message.getBranchId());
                    queueLogService.logSuccess("REPLICATE_BRANCH", getId(message), start);
                }
                case "NEW_CONTRACT_SUPPLIER" -> {
                    setupService.setupContractSupplier(message.getContractSupplierId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CONTRACT_SUPPLIER", getId(message), start);
                }
                case "NEW_CONTRACT_SUBCONTRACTOR" -> {
                    setupService.setupContractSubcontractor(message.getContractSubcontractorId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CONTRACT_SUBCONTRACTOR", getId(message), start);
                }
                case "EMPLOYEE_CONTRACT_SUPPLIER" -> {
                    setupService.setupEmployeeToContractSupplier(message.getContractSupplierId(), message.getEmployeeIds());
                    queueLogService.logSuccess("EMPLOYEE_CONTRACT_SUPPLIER", getId(message), start);
                }
                case "EMPLOYEE_CONTRACT_SUBCONTRACT" -> {
                    setupService.setupEmployeeToContractSubcontract(message.getContractSubcontractorId(), message.getEmployeeIds());
                    queueLogService.logSuccess("EMPLOYEE_CONTRACT_SUBCONTRACT", getId(message), start);
                }
                case "REMOVE_EMPLOYEE_CONTRACT" -> {
                    setupService.setupRemoveEmployeeFromContract(message.getContractId(), message.getEmployeeIds());
                    queueLogService.logSuccess("REMOVE_EMPLOYEE_CONTRACT", getId(message), start);
                }
                default -> throw new IllegalArgumentException("Tipo inválido: " + message.getType());
            }
            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.info("Error {}", e.getMessage());
            queueLogService.logFailure(message.getType(), getId(message), e, start);
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitHandler
    public void consume(ReplicationMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException { // 👈 MUDANÇA
        log.info("Consume ReplicationMessage");
        long start = System.currentTimeMillis();
        try {
            switch (message.getType()) {
                case "REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM" -> {
                    replicationService.setupReplicateDocumentMatrixFromSystem(message.getDocumentId());
                    queueLogService.logSuccess("REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM", getId(message), start);
                }
                case "CREATE_DOCUMENT_MATRIX" -> {
                    replicationService.setupCreateDocumentMatrixReplicateForBranches(message.getDocumentId());
                    queueLogService.logSuccess("CREATE_DOCUMENT_MATRIX", getId(message), start);
                }
                case "CREATE_ACTIVITY" -> {
                    replicationService.replicateCreateActivity(message.getActivityId(), message.getBranchIds());
                    queueLogService.logSuccess("CREATE_ACTIVITY", getId(message), start);
                }
                case "UPDATE_ACTIVITY" -> {
                    replicationService.replicateUpdateActivity(message.getActivityId(), message.getTitle(), message.getActivityRisk(), message.getBranchIds());
                    queueLogService.logSuccess("UPDATE_ACTIVITY", getId(message), start);
                }
                case "DELETE_ACTIVITY" -> {
                    replicationService.replicateDeleteActivity(message.getTitle(), message.getBranchIds());
                    queueLogService.logSuccess("DELETE_ACTIVITY", getId(message), start);
                }
                case "CREATE_SERVICE_TYPE" -> {
                    replicationService.replicateCreateServiceType(message.getServiceTypeBranchId(), message.getBranchIds());
                    queueLogService.logSuccess("CREATE_SERVICE_TYPE", getId(message), start);
                }
                case "UPDATE_SERVICE_TYPE" -> {
                    replicationService.replicateUpdateServiceType(message.getServiceTypeBranchId(), message.getTitle(), message.getServiceTypeRisk(), message.getBranchIds());
                    queueLogService.logSuccess("UPDATE_SERVICE_TYPE", getId(message), start);
                }
                case "DELETE_SERVICE_TYPE" -> {
                    replicationService.replicateDeleteServiceType(message.getTitle(), message.getServiceTypeRisk(), message.getBranchIds());
                    queueLogService.logSuccess("DELETE_SERVICE_TYPE", getId(message), start);
                }
                case "ALLOCATE_DOCUMENT_FROM_ACTIVITY" -> {
                    replicationService.replicateAllocateDocumentToActivity(message.getDocumentId(), message.getActivityId(), message.getBranchIds());
                    queueLogService.logSuccess("ALLOCATE_DOCUMENT_FROM_ACTIVITY", getId(message), start);
                }
                case "DEALLOCATE_DOCUMENT_FROM_ACTIVITY" -> {
                    replicationService.replicateDeallocateDocumentToActivity(message.getDocumentId(), message.getActivityId(), message.getBranchIds());
                    queueLogService.logSuccess("DEALLOCATE_DOCUMENT_FROM_ACTIVITY", getId(message), start);
                }
                case "ALLOCATE_DOCUMENT_FROM_BRANCH" -> {
                    replicationService.replicateAllocateDocumentToBranch(message.getDocumentId(), message.getTitle(), message.getBranchIds());
                    queueLogService.logSuccess("ALLOCATE_DOCUMENT_FROM_BRANCH", getId(message), start);
                }
                case "DEALLOCATE_DOCUMENT_FROM_BRANCH" -> {
                    replicationService.replicateDeallocateDocumentToBranch(message.getDocumentId(), message.getTitle(), message.getBranchIds());
                    queueLogService.logSuccess("DEALLOCATE_DOCUMENT_FROM_BRANCH", getId(message), start);
                }
                case "EXPIRATION_DATE_DOCUMENT_UPDATE" -> {
                    replicationService.replicateExpirationDateDocumentUpdate(message.getDocumentId(), message.getBranchIds());
                    queueLogService.logSuccess("EXPIRATION_DATE_DOCUMENT_UPDATE", getId(message), start);
                }
                default -> throw new IllegalArgumentException("Tipo inválido: " + message.getType());
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            queueLogService.logFailure(message.getType(), getId(message), e, start);
            channel.basicNack(tag, false, false);
        }
    }

    public String getId(SetupMessage msg) {
        return switch (msg.getType()) {
            case "NEW_CLIENT", "NEW_CLIENT_PROFILES" -> msg.getClientId();
            case "NEW_BRANCH", "REPLICATE_BRANCH" -> msg.getBranchId();
            case "NEW_CONTRACT_SUPPLIER", "EMPLOYEE_CONTRACT_SUPPLIER" -> msg.getContractSupplierId();
            case "NEW_CONTRACT_SUBCONTRACTOR", "EMPLOYEE_CONTRACT_SUBCONTRACT" -> msg.getContractSubcontractorId();
            case "REMOVE_EMPLOYEE_CONTRACT" -> msg.getContractId();
            default -> "SEM_ID";
        };
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


