package edu.tamu.weaver.auth.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7779314921709249299L;

    public UserNotFoundException(String message) {
        super(message);
    }

}
