package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.configs.RabbitConfig;
import bl.tech.realiza.services.queue.replication.ReplicationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfig.SETUP_DLQ)
public class SetupDlqConsumer {
    @RabbitHandler
    public void handle(SetupMessage msg) {
        System.err.printf("🔁 DLQ Setup: %s - %s%n", msg.getType(), /* getId(msg) */ "");
    }
    @RabbitHandler
    public void handle(ReplicationMessage msg) {
        System.err.printf("🔁 DLQ Repl: %s - %s%n", msg.getType(), /* getId(msg) */ "");
    }
    // opcional: fallback para payload inesperado
    @RabbitHandler(isDefault = true)
    public void handleRaw(byte[] raw) {
        System.err.printf("🔁 DLQ Raw: %s bytes%n", raw.length);
    }
}

