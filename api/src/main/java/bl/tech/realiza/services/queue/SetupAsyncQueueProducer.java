package bl.tech.realiza.services.queue;

import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetupAsyncQueueProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendSetup(SetupMessage message) {
        rabbitTemplate.convertAndSend("setup-queue", message);
    }
}

