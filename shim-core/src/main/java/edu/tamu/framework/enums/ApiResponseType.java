package edu.tamu.weaver.enums;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 * 
 * @deprecated As of version 1.3.x use edu.tamu.weaver.response.ApiStatus
 *
 */
@Deprecated
public enum ApiResponseType {

    SUCCESS("Your request was successful"),
    REFRESH("Your token has expired"),
    ERROR("Your request caused an error"),
    WARNING("Your request caused warning"),
    INFO("Your request was processed"),
    INVALID("Your request failed validation");

    String message;

    ApiResponseType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
