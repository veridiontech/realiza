package bl.tech.realiza.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String SETUP_QUEUE = "setup-queue";
    public static final String SETUP_DLQ = "setup-queue-dlq";

    @Bean
    public Queue setupDlq() {
        return new Queue("setup-queue-dlq", true);
    }

    @Bean
    public Queue setupQueue() {
        return QueueBuilder.durable("setup-queue")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "setup-queue-dlq")
                .build();
    }
}


