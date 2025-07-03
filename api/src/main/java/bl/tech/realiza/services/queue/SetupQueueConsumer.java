package bl.tech.realiza.services.queue;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.services.setup.SetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetupQueueConsumer {

    private final SetupService setupService;
    private final QueueLogService logService;
    private final ActivityRepository activityRepository;

    @RabbitListener(queues = RabbitConfig.SETUP_QUEUE)
    public void consume(SetupMessage message) {
        long start = System.currentTimeMillis();
        try {
            switch (message.getType()) {
                case "NEW_CLIENT" -> {
                    setupService.setupNewClient(message.getClientId());
                    logService.logSuccess("NEW_CLIENT", message.getClientId(), start);
                }
                case "NEW_BRANCH" -> {
                    setupService.setupBranch(message.getBranchId());
                    logService.logSuccess("NEW_BRANCH", message.getBranchId(), start);
                }
                case "REPLICATE_BRANCH" -> {
                    setupService.setupReplicateBranch(message.getBranchId());
                    logService.logSuccess("REPLICATE_BRANCH", message.getBranchId(), start);
                }
                case "CREATE_ACTIVITY" -> {
                    setupService.replicateCreateActivity(message.getActivityId());
                    logService.logSuccess("CREATE_ACTIVITY", message.getActivityId(), start);
                }
                case "UPDATE_ACTIVITY" -> {
                    setupService.replicateUpdateActivity(message.getActivityId(), message.getTitle(), message.getActivityRisk());
                    logService.logSuccess("UPDATE_ACTIVITY", message.getActivityId(), start);
                }
                case "DELETE_ACTIVITY" -> {
                    setupService.replicateDeleteActivity(message.getClientId(), message.getTitle(), message.getActivityRisk());
                    logService.logSuccess("DELETE_ACTIVITY", message.getActivityId(), start);
                }
                case "CREATE_SERVICE_TYPE" -> {
                    setupService.replicateCreateServiceType(message.getServiceTypeBranchId());
                    logService.logSuccess("CREATE_ACTIVITY", message.getActivityId(), start);
                }
                case "UPDATE_SERVICE_TYPE" -> {
                    setupService.replicateUpdateServiceType(message.getServiceTypeBranchId(), message.getTitle(), message.getServiceTypeRisk());
                    logService.logSuccess("UPDATE_ACTIVITY", message.getActivityId(), start);
                }
                case "DELETE_SERVICE_TYPE" -> {
                    setupService.replicateDeleteServiceType(message.getClientId(), message.getTitle(), message.getServiceTypeRisk());
                    logService.logSuccess("DELETE_ACTIVITY", message.getActivityId(), start);
                }
                case "ALLOCATE_DOCUMENT" -> {
                    setupService.replicateAllocateDocumentToActivity(message.getDocumentId(), message.getActivityId());
                    logService.logSuccess("ALLOCATE_DOCUMENT", message.getDocumentId(), start);
                }
                case "DEALLOCATE_DOCUMENT" -> {
                    setupService.replicateDeallocateDocumentToActivity(message.getDocumentId(), message.getActivityId());
                    logService.logSuccess("DEALLOCATE_DOCUMENT", message.getDocumentId(), start);
                }
                case "NEW_CONTRACT_SUPPLIER" -> {
                    setupService.setupContractSupplier(message.getContractSupplierId(), message.getActivityIds());
                    logService.logSuccess("NEW_CONTRACT_SUPPLIER", message.getContractSupplierId(), start);
                }
                case "NEW_CONTRACT_SUBCONTRACTOR" -> {
                    setupService.setupContractSubcontractor(message.getContractSubcontractorId(), message.getActivityIds());
                    logService.logSuccess("NEW_CONTRACT_SUBCONTRACTOR", message.getContractSubcontractorId(), start);
                }
                case "EMPLOYEE_CONTRACT_SUPPLIER" -> {
                    setupService.setupEmployeeToContractSupplier(message.getContractSupplierId(), message.getEmployeeIds());
                    logService.logSuccess("EMPLOYEE_CONTRACT_SUPPLIER", message.getContractSupplierId(), start);
                }
                case "EMPLOYEE_CONTRACT_SUBCONTRACT" -> {
                    setupService.setupEmployeeToContractSubcontract(message.getContractSubcontractorId(), message.getEmployeeIds());
                    logService.logSuccess("EMPLOYEE_CONTRACT_SUBCONTRACT", message.getContractSubcontractorId(), start);
                }
                case "REMOVE_EMPLOYEE_CONTRACT" -> {
                    setupService.setupRemoveEmployeeFromContract(message.getContractId(), message.getEmployeeIds());
                    logService.logSuccess("REMOVE_EMPLOYEE_CONTRACT", message.getContractId(), start);
                }
                default -> throw new IllegalArgumentException("Tipo inválido: " + message.getType());
            }
        } catch (Exception e) {
            logService.logFailure(message.getType(), getId(message), e, start);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitConfig.SETUP_DLQ)
    public void handleDlq(SetupMessage message) {
        System.err.printf("🔁 Mensagem movida para DLQ: %s - %s%n", message.getType(), getId(message));
    }

    private String getId(SetupMessage msg) {
        return switch (msg.getType()) {
            case "NEW_CLIENT" -> msg.getClientId();
            case "NEW_BRANCH" -> msg.getBranchId();
            case "NEW_CONTRACT_SUPPLIER" -> msg.getContractSupplierId();
            case "NEW_CONTRACT_SUBCONTRACTOR" -> msg.getContractSubcontractorId();
            default -> "SEM_ID";
        };
    }
}


