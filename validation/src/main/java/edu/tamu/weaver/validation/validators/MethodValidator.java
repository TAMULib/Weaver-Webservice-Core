package edu.tamu.weaver.validation.validators;

import edu.tamu.weaver.validation.model.MethodValidationType;

public class MethodValidator extends SimpleValidator<MethodValidationType> {

    private Class<?> clazz;

    private Object[] args;

    private String[] params;

    public MethodValidator() {
    }

    public MethodValidator(MethodValidationType type, String message, Class<?> clazz, String[] params, Object[] args) {
        super(type, message);
        this.clazz = clazz;
        this.params = params;
        this.args = args;
    }

    public MethodValidator(MethodValidationType type, Class<?> clazz, String[] params, Object[] args) {
        this(type, type.getMessage(), clazz, params, args);
    }

    /**
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * @param clazz
     *            the clazz to set
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @param args
     *            the args to set
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * @return the params
     */
    public String[] getParams() {
        return params;
    }

    /**
     * @param params
     *            the params to set
     */
    public void setParams(String[] params) {
        this.params = params;
    }

}
