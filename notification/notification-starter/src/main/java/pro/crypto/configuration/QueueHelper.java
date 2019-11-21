package pro.crypto.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import pro.crypto.properties.MessageRetryConfig;

class QueueHelper {

    static SimpleMessageListenerContainer createListener(String queueName, ConnectionFactory factory, Integer inboundConcurrentConsumers) {
        Queue queue = new Queue(queueName);
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(factory);
        listenerContainer.setQueues(queue);
        listenerContainer.setExclusive(false);
        listenerContainer.setConcurrentConsumers(inboundConcurrentConsumers);
        return listenerContainer;
    }

    static RetryTemplate createRetryTemplate(MessageRetryConfig properties) {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(properties.getMaxAttempts());
        retryTemplate.setRetryPolicy(policy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getInitialInterval());
        backOffPolicy.setMultiplier(properties.getMultiplier());
        backOffPolicy.setMaxInterval(properties.getMaxInterval());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

}
