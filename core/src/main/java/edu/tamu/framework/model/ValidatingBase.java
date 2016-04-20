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

import edu.tamu.framework.validation.ModelBindingResult;

public abstract class ValidatingBase {

    @Transient
    @JsonIgnore
    protected ModelBindingResult bindingResult;

    /**
     * @return the bindingResult
     */
    public ModelBindingResult getBindingResult() {
        return bindingResult;
    }

    /**
     * @param bindingResult
     *            the bindingResult to set
     */
    public void setBindingResult(ModelBindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

}
