/* 
 * BusinessValidationType.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.enums;

public enum BusinessValidationType {

    CREATE("Unable to create model"),
    READ("Unable to read model"),
    UPDATE("Unable to update model"),
    DELETE("Unable to delete model"),
    RESET("Unable to reset model"),
    EXISTS("Model already exists"),
    NONEXISTS("Model does not exist");

    String message;

    BusinessValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
