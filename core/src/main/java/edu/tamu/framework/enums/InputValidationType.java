package edu.tamu.framework.enums;

public enum InputValidationType {
    
    minLength("Input minimum length not reached"),
    maxLength("Input is over maximum length"),
    required("Input is required"),
    pattern("Input pattern does not match");

    String message;

    InputValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
}
