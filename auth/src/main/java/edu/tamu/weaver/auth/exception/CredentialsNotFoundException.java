package edu.tamu.weaver.auth.exception;

public class CredentialsNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4128620872122571673L;

    public CredentialsNotFoundException(String message) {
        super(message);
    }

}
