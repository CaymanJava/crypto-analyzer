package pro.crypto.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import pro.crypto.model.EmailQueue;
import pro.crypto.properties.MailProperties;
import pro.crypto.properties.MessageRetryConfig;
import pro.crypto.service.MailService;

@Configuration
@AllArgsConstructor
public class MailIncomingQueueConfiguration {

    private final ConnectionFactory connectionFactory;
    private final ObjectMapper mapper;
    private final MailService service;
    private final MailProperties properties;
    private final MessageRetryConfig retryConfig;

    @Bean
    public Queue mailIncomingQueue() {
        return QueueBuilder
                .durable(properties.getQueueName())
                .withArgument("x-max-priority", 100)
                .build();
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(properties.getExchange());
    }

    @Bean
    public SimpleMessageListenerContainer mailListener() {
        return QueueHelper.createListener(properties.getQueueName(), this.connectionFactory, properties.getInboundConcurrentConsumers());
    }

    @Bean
    public IntegrationFlow mailInboundFlow() {
        return IntegrationFlows.from(Amqp.inboundAdapter(mailListener()))
                .transform(Transformers.fromJson(EmailQueue.class, new Jackson2JsonObjectMapper(mapper)))
                .handle(service, "processEmailMessage", c -> c.advice(retryAdvice())).get();
    }

    @Bean
    Binding mailBinding() {
        return BindingBuilder
                .bind(mailIncomingQueue())
                .to(mailExchange())
                .with(properties.getRoutingKey());
    }

    private Advice retryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        advice.setRetryTemplate(QueueHelper.createRetryTemplate(retryConfig));
        return advice;
    }

}
