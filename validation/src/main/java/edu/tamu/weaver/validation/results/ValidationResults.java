package edu.tamu.weaver.validation.results;

import java.util.HashMap;
import java.util.Map;

public class ValidationResults {

    private boolean valid;

    private Map<String, Map<String, String>> messages;

    public ValidationResults() {
        valid = true;
        messages = new HashMap<String, Map<String, String>>();
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid
     *            the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the messages
     */
    public Map<String, Map<String, String>> getMessages() {
        return messages;
    }

    /**
     * @param messages
     *            the messages to set
     */
    public void setMessages(Map<String, Map<String, String>> messages) {
        this.messages = messages;
    }

    public void addMessage(String key, String type, String message) {
        Map<String, String> messages = this.messages.get(key);
        if (messages == null) {
            messages = new HashMap<String, String>();
        }
        messages.put(type, message);
        this.messages.put(key, messages);
    }

}
