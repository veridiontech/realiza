package bl.tech.realiza.services.queue;

import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.services.setup.SetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetupQueueConsumer {

    private final SetupService setupService;

    @RabbitListener(queues = "setup-queue")
    public void consume(SetupMessage message) {
        switch (message.getType()) {
            case "NEW_CLIENT" -> setupService.setupNewClient(message.getClient());
            case "NEW_BRANCH" -> setupService.setupBranch(message.getBranch());
            case "NEW_CONTRACT_SUPPLIER" -> setupService.setupContractSupplier(message.getContractSupplier(), message.getActivitiesId());
            case "NEW_CONTRACT_SUBCONTRACTOR" -> setupService.setupContractSubcontractor(message.getContractSubcontractor(), message.getActivitiesId());
        }
    }
}


