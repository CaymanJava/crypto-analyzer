package pro.crypto.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import pro.crypto.properties.SmsProperties;

import static org.springframework.amqp.core.MessageDeliveryMode.PERSISTENT;

@Configuration
public class SmsOutboundConfiguration {

    private final AmqpTemplate amqpTemplate;
    private final SmsProperties properties;

    public SmsOutboundConfiguration(AmqpTemplate amqpTemplate, SmsProperties properties, RabbitTemplate rabbitTemplate) {
        this.amqpTemplate = amqpTemplate;
        this.properties = properties;
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    @Bean
    public IntegrationFlow smsOutboundFlow() {
        return IntegrationFlows.from(smsInputChannel())
                .handle(Amqp.outboundAdapter(amqpTemplate)
                        .exchangeName(properties.getExchange())
                        .routingKey(properties.getRoutingKey())
                        .mappedRequestHeaders("priority")
                        .defaultDeliveryMode(PERSISTENT))
                .get();
    }

    @Bean
    @Qualifier("smsMessagingTemplate")
    public MessagingTemplate smsMessagingTemplate() {
        MessagingTemplate messagingTemplate = new MessagingTemplate(smsInputChannel());
        messagingTemplate.setSendTimeout(5000L);
        messagingTemplate.setReceiveTimeout(5000L);
        return messagingTemplate;
    }

    @Bean
    public DirectChannel smsInputChannel() {
        return MessageChannels.direct().get();
    }

}
