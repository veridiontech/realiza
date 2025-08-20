package bl.tech.realiza.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@Slf4j
@Configuration
public class RabbitConfig {

    public static final String SETUP_QUEUE = "setup-queue";
    public static final String SETUP_DLQ = "setup-queue-dlq";
    public static final String REPLICATION_QUEUE = "replication-queue";
    public static final String REPLICATION_DLQ = "replication-queue-dlq";


    @Bean
    public Queue setupDlq() {
        return new Queue(SETUP_DLQ, true);
    }

    @Bean
    public Queue replicationDlq() {
        return new Queue(REPLICATION_DLQ, true);
    }

    @Bean
    public Queue setupQueue() {
        return QueueBuilder.durable(SETUP_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", SETUP_DLQ)
                .build();
    }

    @Bean
    public Queue replicationQueue() {
        return QueueBuilder.durable(REPLICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", REPLICATION_DLQ)
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        var typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages(
                "bl.tech.realiza.services.queue.setup",
                "bl.tech.realiza.services.queue.replication"
        );

        var converter = new Jackson2JsonMessageConverter();
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter,
            ErrorHandler errorHandler
    ) {
        if (connectionFactory instanceof CachingConnectionFactory ccf) {
            ccf.setConnectionNameStrategy(f -> "realiza-setup-" + System.getenv("HOSTNAME"));
        }

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setErrorHandler(errorHandler);
        factory.setContainerCustomizer(container -> container.setShutdownTimeout(600_000));

        return factory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(t -> {
            log.error("Error processing message from queue. Cause: {}", t.getCause().getMessage(), t);
            return new ConditionalRejectingErrorHandler.DefaultExceptionStrategy().isFatal(t);
        });
    }
}
