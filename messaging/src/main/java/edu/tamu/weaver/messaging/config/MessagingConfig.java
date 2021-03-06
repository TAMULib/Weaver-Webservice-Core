package edu.tamu.weaver.messaging.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@Profile("weaver-messaging")
public class MessagingConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String brokerUser = null;

    @Value("${spring.activemq.password}")
    private String brokerPassword = null;

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(defaultActiveMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTopicTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    private ActiveMQConnectionFactory defaultActiveMQConnectionFactory() {
        return new ActiveMQConnectionFactory(brokerUser, brokerPassword, brokerUrl);
    }

    @Bean
    public JmsListenerContainerFactory<?> topicContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(defaultActiveMQConnectionFactory());
        factory.setPubSubDomain(true);
        return factory;
    }

}
