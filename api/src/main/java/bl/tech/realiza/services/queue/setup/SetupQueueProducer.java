package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetupQueueProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(SetupMessage message) {
        rabbitTemplate.convertAndSend(RabbitConfig.SETUP_QUEUE, message);
    }
}

