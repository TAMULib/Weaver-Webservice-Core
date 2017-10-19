package edu.tamu.weaver.token.exception;

/**
 * Expired token exception
 */
public class ExpiredTokenException extends TokenException {

    private static final long serialVersionUID = -1454294645802295919L;

    public ExpiredTokenException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

}
