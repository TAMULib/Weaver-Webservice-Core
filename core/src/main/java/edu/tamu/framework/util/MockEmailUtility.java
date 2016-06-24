package edu.tamu.framework.util;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

/**
 * Mock Email Utility
 */
@Service
public class MockEmailUtility extends CoreEmailUtility {

    public MockEmailUtility() {
        super();
    }

    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        // set message on request to assert in unit test
    }

}