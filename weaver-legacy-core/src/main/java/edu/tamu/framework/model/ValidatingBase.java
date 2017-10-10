/* 
 * ValidatingBase.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.framework.validation.ValidationResults;
import edu.tamu.framework.validation.Validator;

@Deprecated
public abstract class ValidatingBase {

    @Transient
    @JsonIgnore
    protected Validator modelValidator;

    /**
     * @return the modelValidator
     */
    @JsonIgnore
    public Validator getModelValidator() {
        return modelValidator;
    }

    /**
     * @param modelValidator
     *            the modelValidator to set
     */
    @JsonIgnore
    public void setModelValidator(Validator modelValidator) {
        this.modelValidator = modelValidator;
    }

    public <U extends ValidatingBase> ValidationResults validate(U model) {
        return modelValidator.validate(model);
    }

}
