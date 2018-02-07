package edu.tamu.weaver.validation.model;

public enum InputValidationType {

    // @formatter:off
    minlength("Input minimum length not reached"),
    maxlength("Input is over maximum length"),
    required("Input is required"),
    pattern("Input pattern does not match");
    // @formatter:on

    String message;

    InputValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
