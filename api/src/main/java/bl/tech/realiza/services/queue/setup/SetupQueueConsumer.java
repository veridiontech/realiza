package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.QueueLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetupQueueConsumer {

    private final SetupServiceFacade setupService;
    private final QueueLogService queueLogService;

    @RabbitListener(queues = RabbitConfig.SETUP_QUEUE)
    public void consume(SetupMessage message) {
        long start = System.currentTimeMillis();
        log.info("Message received: {}", message.getType());
        try {
            switch (message.getType()) {
                case "NEW_CLIENT" -> {
                    log.info("New client received");
                    setupService.setupNewClient(message.getClientId());
                    queueLogService.logSuccess("NEW_CLIENT", message.getClientId(), start);
                }
                case "NEW_CLIENT_PROFILES" -> {
                    log.info("New client profiles received");
                    setupService.setupNewClientProfiles(message.getClientId());
                    queueLogService.logSuccess("NEW_CLIENT_PROFILES", message.getClientId(), start);
                }
                case "NEW_BRANCH" -> {
                    log.info("New branch received");
                    setupService.setupBranch(message.getBranchId());
                    queueLogService.logSuccess("NEW_BRANCH", message.getBranchId(), start);
                }
                case "REPLICATE_BRANCH" -> {
                    log.info("Replicate branch received");
                    setupService.setupReplicateBranch(message.getBranchId());
                    queueLogService.logSuccess("REPLICATE_BRANCH", message.getBranchId(), start);
                }
                case "NEW_CONTRACT_SUPPLIER" -> {
                    log.info("New contract supplier received");
                    setupService.setupContractSupplier(message.getContractSupplierId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CONTRACT_SUPPLIER", message.getContractSupplierId(), start);
                }
                case "NEW_CONTRACT_SUBCONTRACTOR" -> {
                    log.info("New contract subcontractor received");
                    setupService.setupContractSubcontractor(message.getContractSubcontractorId(), message.getActivityIds());
                    queueLogService.logSuccess("NEW_CONTRACT_SUBCONTRACTOR", message.getContractSubcontractorId(), start);
                }
                case "EMPLOYEE_CONTRACT_SUPPLIER" -> {
                    log.info("New employees added to contract supplier received");
                    setupService.setupEmployeeToContractSupplier(message.getContractSupplierId(), message.getEmployeeIds());
                    queueLogService.logSuccess("EMPLOYEE_CONTRACT_SUPPLIER", message.getContractSupplierId(), start);
                }
                case "EMPLOYEE_CONTRACT_SUBCONTRACT" -> {
                    log.info("New employees added to contract subcontractor received");
                    setupService.setupEmployeeToContractSubcontract(message.getContractSubcontractorId(), message.getEmployeeIds());
                    queueLogService.logSuccess("EMPLOYEE_CONTRACT_SUBCONTRACT", message.getContractSubcontractorId(), start);
                }
                case "REMOVE_EMPLOYEE_CONTRACT" -> {
                    log.info("Employees removed from contract supplier received");
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

    @RabbitListener(queues = RabbitConfig.SETUP_DLQ)
    public void handleDlq(SetupMessage failedMessage) {
        try {
            log.error("🔁 Mensagem movida para DLQ: {} - ID: {}",
                    failedMessage.getType(),
                    getId(failedMessage));
        } catch (Exception e) {
            log.error("Erro fatal ao processar mensagem da DLQ: {}", failedMessage, e);
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
}

