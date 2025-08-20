package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetupQueueProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(SetupMessage message) {
        log.info("Message received {} and produced", message.getType());
        rabbitTemplate.convertAndSend("", RabbitConfig.SETUP_QUEUE, message);
    }
}

