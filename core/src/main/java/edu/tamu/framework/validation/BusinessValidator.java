package edu.tamu.framework.validation;

import edu.tamu.framework.enums.BusinessValidationType;

public class BusinessValidator extends SimpleValidator<BusinessValidationType> {
    
    private String[] params;
    
    public BusinessValidator() {}
        
    public BusinessValidator(BusinessValidationType type, String message, String... params) {
        super(type, message);
        this.params = params;
    }
    
    public BusinessValidator(BusinessValidationType type, String... params) {
        this(type, type.getMessage(), params);
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
