/* 
 * BusinessValidator.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.validation;

import edu.tamu.weaver.enums.BusinessValidationType;

@Deprecated
public class BusinessValidator extends SimpleValidator<BusinessValidationType> {

    private Class<?>[] joins;

    private String[] params;

    private String[] path;

    private String restrict;

    public BusinessValidator() {
    }

    public BusinessValidator(BusinessValidationType type, String message, Class<?>[] joins, String[] params) {
        super(type, message);
        this.joins = joins;
        this.params = params;
    }

    public BusinessValidator(BusinessValidationType type, Class<?>[] joins, String[] params) {
        this(type, type.getMessage(), joins, params);
    }

    public BusinessValidator(BusinessValidationType type, Class<?>[] joins, String[] params, String[] path, String restrict) {
        this(type, type.getMessage(), joins, params);
        this.path = path;
        this.restrict = restrict;
    }

    /**
     * @return the joins
     */
    public Class<?>[] getJoins() {
        return joins;
    }

    /**
     * @param params
     *            the params to set
     */
    public void setJoins(Class<?>[] joins) {
        this.joins = joins;
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

    /**
     * @return the path
     */
    public String[] getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String[] path) {
        this.path = path;
    }

    /**
     * @return the restrict
     */
    public String getRestrict() {
        return restrict;
    }

    /**
     * @param restrict
     *            the restrict to set
     */
    public void setRestrict(String restrict) {
        this.restrict = restrict;
    }

}
