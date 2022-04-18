package edu.tamu.weaver.email.service;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;

public class MockEmailService extends WeaverEmailService {

    public MockEmailService() {
        super();
    }

    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        // set message on request to assert in unit test
    }

}
