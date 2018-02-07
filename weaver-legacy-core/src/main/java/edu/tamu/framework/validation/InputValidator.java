/* 
 * InputValidator.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.validation;

import edu.tamu.weaver.enums.InputValidationType;

@Deprecated
public class InputValidator extends SimpleValidator<InputValidationType> {

    private String property;

    private Object value;

    public InputValidator() {
    }

    public InputValidator(InputValidationType type, String message, String property, Object value) {
        super(type, message);
        this.property = property;
        this.value = value;
    }

    public InputValidator(InputValidationType type, String property, Object value) {
        this(type, type.getMessage(), property, value);
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property
     *            the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(this.getClass())) {
            return this.getType() == ((BaseValidator<InputValidationType>) obj).getType() && this.getProperty().equals(((InputValidator) obj).getProperty());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (getType() == null ? 0 : getType().hashCode());
        return hashCode;
    }

}
