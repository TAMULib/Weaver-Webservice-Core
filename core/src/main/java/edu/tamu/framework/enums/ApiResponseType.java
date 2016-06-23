/* 
 * ApiResponseType.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.enums;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public enum ApiResponseType {

    SUCCESS("Your request was successful"),
    REFRESH("Your token has expired"),
    ERROR("Your request caused an error"),
    VALIDATION_ERROR("Your request caused a validation error"),
    WARNING("Your request caused warning"),
    VALIDATION_WARNING("Your request caused a validation warning"),
    INFO("Your request was processed"),
    VALIDATION_INFO("Your validation request was processed");

    String message;

    ApiResponseType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
