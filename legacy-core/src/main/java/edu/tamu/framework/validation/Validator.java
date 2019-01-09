/* 
 * Validator.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.validation;

import java.util.List;
import java.util.Map;

import edu.tamu.weaver.model.ValidatingBase;

@Deprecated
public interface Validator {

    public <U extends ValidatingBase> ValidationResults validate(U model);

    public Map<String, List<InputValidator>> getInputValidators();

    public List<BusinessValidator> getBusinessValidators();

}
