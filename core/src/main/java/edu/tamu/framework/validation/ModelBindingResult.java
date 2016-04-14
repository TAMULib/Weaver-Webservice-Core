package edu.tamu.framework.validation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

public class ModelBindingResult extends BeanPropertyBindingResult {

	private static final long serialVersionUID = 2834743206746612206L;
	
	private final List<ObjectError> warnings = new LinkedList<ObjectError>();
	
	private final List<ObjectError> infos = new LinkedList<ObjectError>();

	public ModelBindingResult(Object target, String objectName) {
		super(target, objectName);
		
	}
	
	public void addWarning(ObjectError warning) {
		this.warnings.add(warning);
	}
	
	public boolean hasWarnings() {
		return !this.warnings.isEmpty();
	}
	
	public List<ObjectError> getAllWarnings() {
		return Collections.unmodifiableList(this.warnings);
	}
	
	public void addInfo(ObjectError warning) {
		this.infos.add(warning);
	}
	
	public boolean hasInfos() {
		return !this.infos.isEmpty();
	}
	
	public List<ObjectError> getAllInfos() {
		return Collections.unmodifiableList(this.infos);
	}
	
	public List<ObjectError> getAll() {
	    List<ObjectError> ret = new LinkedList<ObjectError>();
	    ret.addAll(this.infos);
	    ret.addAll(this.warnings);
	    ret.addAll(super.getAllErrors());
	    return Collections.unmodifiableList(ret);
	}
	
	public List<ObjectError> getAllWarningsAndInfos() {
        List<ObjectError> ret = new LinkedList<ObjectError>();
        ret.addAll(this.infos);
        ret.addAll(this.warnings);
        return Collections.unmodifiableList(ret);
    }

}
