package edu.tamu.weaver.validation.validators;

import java.util.List;
import java.util.Map;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;
import edu.tamu.weaver.validation.results.ValidationResults;

public interface Validator {

    public <U extends ValidatingBaseEntity> ValidationResults validate(U model);

    public Map<String, List<InputValidator>> getInputValidators();

    public List<BusinessValidator> getBusinessValidators();

}
