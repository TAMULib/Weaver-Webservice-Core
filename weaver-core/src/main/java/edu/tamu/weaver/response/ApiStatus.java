package edu.tamu.weaver.response;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public enum ApiStatus {

    // @formatter:off
    SUCCESS("Your request was successful"),
    REFRESH("Your token has expired"),
    ERROR("Your request caused an error"),
    WARNING("Your request caused warning"),
    INFO("Your request was processed"),
    INVALID("Your request failed validation");
    // @formatter:on

    String message;

    ApiStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
