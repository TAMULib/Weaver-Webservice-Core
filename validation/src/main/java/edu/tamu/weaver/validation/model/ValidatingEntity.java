package edu.tamu.weaver.validation.model;

import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.validators.Validator;

public interface ValidatingEntity {

    public Validator getModelValidator();

    public void setModelValidator(Validator modelValidator);

    public <U extends ValidatingEntity> ValidationResults validate(U model);
	
}
