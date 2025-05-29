package bl.tech.realiza.services.queue;

import bl.tech.realiza.configs.RabbitConfig;
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

    @RabbitListener(queues = RabbitConfig.SETUP_QUEUE)
    public void consume(SetupMessage message) {
        try {
            switch (message.getType()) {
                case "NEW_CLIENT" -> {
                    setupService.setupNewClient(message.getClient());
                    logService.logSuccess("NEW_CLIENT", message.getClient().getIdClient());
                }
                case "NEW_BRANCH" -> {
                    setupService.setupBranch(message.getBranch());
                    logService.logSuccess("NEW_BRANCH", message.getBranch().getIdBranch());
                }
                case "NEW_CONTRACT_SUPPLIER" -> {
                    setupService.setupContractSupplier(message.getContractSupplier(), message.getActivitiesId());
                    logService.logSuccess("NEW_CONTRACT_SUPPLIER", message.getContractSupplier().getIdContract());
                }
                case "NEW_CONTRACT_SUBCONTRACTOR" -> {
                    setupService.setupContractSubcontractor(message.getContractSubcontractor(), message.getActivitiesId());
                    logService.logSuccess("NEW_CONTRACT_SUBCONTRACTOR", message.getContractSubcontractor().getIdContract());
                }
                case "EMPLOYEE_CONTRACT_SUPPLIER" -> {
                    setupService.setupEmployeeToContractSupplier(message.getContractSupplier(), message.getEmployees());
                    logService.logSuccess("EMPLOYEE_CONTRACT_SUPPLIER", message.getContractSupplier().getIdContract());
                }
                case "EMPLOYEE_CONTRACT_SUBCONTRACT" -> {
                    setupService.setupEmployeeToContractSubcontract(message.getContractSubcontractor(), message.getEmployees());
                    logService.logSuccess("EMPLOYEE_CONTRACT_SUBCONTRACT", message.getContractSubcontractor().getIdContract());
                }
                default -> throw new IllegalArgumentException("Tipo inválido: " + message.getType());
            }
        } catch (Exception e) {
            logService.logFailure(message.getType(), getId(message), e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitConfig.SETUP_DLQ)
    public void handleDlq(SetupMessage message) {
        System.err.printf("🔁 Mensagem movida para DLQ: %s - %s%n", message.getType(), getId(message));
    }


    private String getId(SetupMessage msg) {
        return switch (msg.getType()) {
            case "NEW_CLIENT" -> msg.getClient().getIdClient();
            case "NEW_BRANCH" -> msg.getBranch().getIdBranch();
            case "NEW_CONTRACT_SUPPLIER" -> msg.getContractSupplier().getIdContract();
            case "NEW_CONTRACT_SUBCONTRACTOR" -> msg.getContractSubcontractor().getIdContract();
            default -> "SEM_ID";
        };
    }
}


