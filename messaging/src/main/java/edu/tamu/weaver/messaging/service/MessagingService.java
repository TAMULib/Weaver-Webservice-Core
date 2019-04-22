package edu.tamu.weaver.messaging.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    private final JmsMessagingTemplate jmsTemplate;

    @Autowired
    public MessagingService(JmsMessagingTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String destination, Map<String, String> payload) {
        Message<Map<String, String>> message = MessageBuilder.withPayload(payload).build();
        this.jmsTemplate.send(destination, message);
    }

}