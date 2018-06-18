package edu.tamu.weaver.validation.model;

public enum MethodValidationType {

    // @formatter:off
    REORDER("Unable to reorder"),
    LIST_REORDER("Unable to reorder list"),
    SORT("Unable to sort");
    // @formatter:on

    String message;

    MethodValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
