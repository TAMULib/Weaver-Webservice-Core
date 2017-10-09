/* 
 * BaseValidator.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.validation;

public abstract class BaseValidator<T> {

    protected T type;

    protected String message;

    /**
     * @return the type
     */
    public T getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(T type) {
        this.type = type;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
