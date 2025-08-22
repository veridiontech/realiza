package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.replication.ReplicationMessage;
import bl.tech.realiza.services.queue.replication.ReplicationQueueConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfig.SETUP_DLQ)
public class ReplicationDlqConsumer {

    private final ReplicationQueueConsumer replicationQueueConsumer;

    @RabbitHandler
    public void handle(ReplicationMessage msg) {
        System.err.printf("🔁 DLQ Repl: %s - %s%n", msg.getType(), replicationQueueConsumer.getId(msg));
    }
    // opcional: fallback para payload inesperado
    @RabbitHandler(isDefault = true)
    public void handleRaw(byte[] raw) {
        System.err.printf("🔁 DLQ Raw: %s bytes%n", raw.length);
    }
}

