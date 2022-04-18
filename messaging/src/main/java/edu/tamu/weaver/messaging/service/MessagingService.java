package edu.tamu.weaver.messaging.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Profile("weaver-messaging")
public class MessagingService {

    private final JmsMessagingTemplate jmsTemplate;

    @Autowired
    public MessagingService(JmsTemplate jmsTopicTemplate) {
        this.jmsTemplate = new JmsMessagingTemplate(jmsTopicTemplate);
    }

    public void sendMessage(String destination, Map<String, String> payload) {
        Message<Map<String, String>> message = MessageBuilder.withPayload(payload).build();
        this.jmsTemplate.send(destination, message);
    }

}
