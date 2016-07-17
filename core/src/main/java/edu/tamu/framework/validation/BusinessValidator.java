package edu.tamu.framework.validation;

import edu.tamu.framework.enums.BusinessValidationType;

public class BusinessValidator extends SimpleValidator<BusinessValidationType> {
    
	private Class<?>[] joins;
	
    private String[] params;
    
    public BusinessValidator() {}
        
    public BusinessValidator(BusinessValidationType type, String message, Class<?>[] joins, String[] params) {
        super(type, message);
        this.joins = joins;
        this.params = params;
    }
    
    public BusinessValidator(BusinessValidationType type, Class<?>[] joins, String[] params) {
        this(type, type.getMessage(), joins, params);
    }
    
    /**
     * @return the joins
     */
    public Class<?>[] getJoins() {
        return joins;
    }

    /**
     * @param params the params to set
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
     * @param params the params to set
     */
    public void setParams(String[] params) {
        this.params = params;
    }

}
