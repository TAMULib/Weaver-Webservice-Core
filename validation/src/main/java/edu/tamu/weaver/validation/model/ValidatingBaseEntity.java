package edu.tamu.weaver.validation.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.weaver.data.model.BaseEntity;
import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.validators.Validator;

@MappedSuperclass
public abstract class ValidatingBaseEntity extends BaseEntity implements ValidatingEntity {

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

    public <U extends ValidatingEntity> ValidationResults validate(U model) {
        return modelValidator.validate(model);
    }

}
