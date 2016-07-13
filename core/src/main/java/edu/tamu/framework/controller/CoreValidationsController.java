package edu.tamu.framework.controller;

import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.model.ApiResponse;

public abstract class CoreValidationsController {
	
	protected static final String MODEL_VALIDATOR_FIELD = "modelValidator";

	public abstract ApiResponse validations(@ApiVariable String entityName);
	
}
