/* 
 * MethodValidationType.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.enums;

@Deprecated
public enum MethodValidationType {
        
    REORDER("Unable to reorder"),
    LIST_REORDER("Unable to reorder list"),
    SORT("Unable to sort");

    String message;

    MethodValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
}
