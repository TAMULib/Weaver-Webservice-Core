package edu.tamu.framework.validation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

public class ModelBindingResult extends BeanPropertyBindingResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2834743206746612206L;
	
	private final List<ObjectError> warnings = new LinkedList<ObjectError>();
	private final List<ObjectError> infos = new LinkedList<ObjectError>();

	public ModelBindingResult(Object target, String objectName) {
		super(target, objectName);
		
	}
	
	public void addWarning(ObjectError warning) {
		this.warnings.add(warning);
	}
	
	public boolean hasWarning() {
		return !this.warnings.isEmpty();
	}
	
	public List<ObjectError> getAllWarnings() {
		return Collections.unmodifiableList(this.warnings);
	}
	
	public void addInfo(ObjectError warning) {
		this.infos.add(warning);
	}
	
	public boolean hasInfo() {
		return !this.infos.isEmpty();
	}
	
	public List<ObjectError> getAllInfos() {
		return Collections.unmodifiableList(this.infos);
	}

}
