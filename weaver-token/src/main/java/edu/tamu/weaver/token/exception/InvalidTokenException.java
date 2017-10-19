package edu.tamu.weaver.token.exception;

/**
 * Invalid token exception
 */
public class InvalidTokenException extends TokenException {

    private static final long serialVersionUID = 5903450397447821993L;

    public InvalidTokenException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

}
