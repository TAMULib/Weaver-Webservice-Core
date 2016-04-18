/* 
 * Base.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import org.springframework.validation.BeanPropertyBindingResult;

public abstract class Base {

    protected BeanPropertyBindingResult bindingResult;

    /**
     * @return the bindingResult
     */
    public BeanPropertyBindingResult getBindingResult() {
        return bindingResult;
    }

    /**
     * @param bindingResult
     *            the bindingResult to set
     */
    public void setBindingResult(BeanPropertyBindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

}
