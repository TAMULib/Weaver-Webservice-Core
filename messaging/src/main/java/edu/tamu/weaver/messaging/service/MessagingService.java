package edu.tamu.weaver.messaging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.weaver.messaging.model.MessageActions;

@Service
public class MessagingService {

    private final JmsMessagingTemplate jmsTemplate;

    @Autowired
    public MessagingService(JmsMessagingTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String destination, JsonNode payload, MessageActions action) {
        Message<JsonNode> message = MessageBuilder.withPayload(payload).setHeader("action", action).build();
        this.jmsTemplate.send(destination, message);
    }

}