package bl.tech.realiza.services.queue.replication;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.setup.SetupMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplicationQueueProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(ReplicationMessage message) {
        rabbitTemplate.convertAndSend(RabbitConfig.REPLICATION_QUEUE, message);
    }
}

