package edu.tamu.framework.validation;

import java.util.List;
import java.util.Map;

import edu.tamu.framework.model.ValidatingBase;
import edu.tamu.framework.validation.ValidationResults;

public interface Validator {
    
    public <U extends ValidatingBase> ValidationResults validate(U model);
    
    public Map<String, List<InputValidator>> getInputValidators();
    
    public List<BusinessValidator> getBusinessValidators();

}
