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
import pro.crypto.model.SmsQueue;
import pro.crypto.properties.MessageRetryConfig;
import pro.crypto.properties.SmsProperties;
import pro.crypto.service.SmsService;

@Configuration
@AllArgsConstructor
public class SmsIncomingQueueConfiguration {

    private final ConnectionFactory connectionFactory;
    private final ObjectMapper mapper;
    private final SmsService service;
    private final SmsProperties properties;
    private final MessageRetryConfig retryConfig;

    @Bean
    public Queue smsIncomingQueue() {
        return QueueBuilder
                .durable(properties.getQueueName())
                .withArgument("x-max-priority", 100)
                .build();
    }

    @Bean
    public DirectExchange smsExchange() {
        return new DirectExchange(properties.getExchange());
    }

    @Bean
    public SimpleMessageListenerContainer smsListener() {
        return QueueHelper.createListener(properties.getQueueName(), this.connectionFactory, properties.getInboundConcurrentConsumers());
    }

    @Bean
    public IntegrationFlow smsInboundFlow() {
        return IntegrationFlows.from(Amqp.inboundAdapter(smsListener()))
                .transform(Transformers.fromJson(SmsQueue.class, new Jackson2JsonObjectMapper(mapper)))
                .handle(service, "processSms", c -> c.advice(retryAdvice())).get();
    }

    @Bean
    Binding smsBinding() {
        return BindingBuilder
                .bind(smsIncomingQueue())
                .to(smsExchange())
                .with(properties.getRoutingKey());
    }

    private Advice retryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        advice.setRetryTemplate(QueueHelper.createRetryTemplate(retryConfig));
        return advice;
    }

}
