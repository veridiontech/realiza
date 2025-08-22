package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.QueueLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetupQueueConsumer {

    private final SetupService setupService;
    private final QueueLogService queueLogService;

    @RabbitListener(queues = RabbitConfig.SETUP_QUEUE)
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
                    setupService.setupBranch(message.getBranchId());
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

    @RabbitListener(queues = RabbitConfig.SETUP_DLQ)
    public void handleDlq(SetupMessage message) {
        System.err.printf("🔁 Mensagem Setup movida para DLQ: %s - %s%n", message.getType(), getId(message));
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

