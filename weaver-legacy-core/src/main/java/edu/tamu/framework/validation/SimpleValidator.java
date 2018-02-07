/* 
 * SimpleValidator.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.validation;

@Deprecated
public class SimpleValidator<T> extends BaseValidator<T> {

    public SimpleValidator() {
    }

    public SimpleValidator(T type, String message) {
        this();
        this.type = type;
        this.message = message;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(this.getClass())) {
            return this.getType() == ((BaseValidator<T>) obj).getType();
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
