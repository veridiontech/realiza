package bl.tech.realiza.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue setupQueue() {
        return new Queue("setup-queue", true); // durable
    }
}


