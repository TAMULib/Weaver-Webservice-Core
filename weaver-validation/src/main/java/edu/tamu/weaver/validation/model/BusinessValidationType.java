package edu.tamu.weaver.validation.model;

public enum BusinessValidationType {

    // @formatter:off
    CREATE("Unable to create model"),
    READ("Unable to read model"),
    UPDATE("Unable to update model"),
    DELETE("Unable to delete model"),
    RESET("Unable to reset model");
    // @formatter:on

    String message;

    BusinessValidationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
