package edu.tamu.weaver.validation.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;
import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.utility.ValidationUtility;

public abstract class BaseModelValidator implements Validator {

    private Map<String, List<InputValidator>> inputValidators = new HashMap<String, List<InputValidator>>();

    private List<BusinessValidator> businessValidators = new ArrayList<BusinessValidator>();

    @Override
    public <U extends ValidatingBaseEntity> ValidationResults validate(U model) {

        ValidationResults validationResults = new ValidationResults();

        // input validators

        for (Entry<String, List<InputValidator>> entry : inputValidators.entrySet()) {
            List<InputValidator> inputValidators = entry.getValue();
            inputValidators.forEach(inputValidator -> {
                ValidationUtility.aggregateValidationResults(validationResults, ValidationUtility.validateInputs(inputValidator, model));
            });
        }

        // business validators

        businessValidators.forEach(businessValidator -> {
            ValidationUtility.aggregateValidationResults(validationResults, ValidationUtility.validateBusiness(businessValidator, model));
        });

        return validationResults;
    }

    /**
     * @return the inputValidators
     */
    public Map<String, List<InputValidator>> getInputValidators() {
        return inputValidators;
    }

    /**
     * @param inputValidators
     *            the inputValidators to set
     */
    public void setInputValidators(Map<String, List<InputValidator>> inputValidators) {
        this.inputValidators = inputValidators;
    }

    /**
     * @return the businessValidators
     */
    public List<BusinessValidator> getBusinessValidators() {
        return businessValidators;
    }

    /**
     * @param businessValidators
     *            the businessValidators to set
     */
    public void setBusinessValidators(List<BusinessValidator> businessValidators) {
        this.businessValidators = businessValidators;
    }

    public void addInputValidator(InputValidator inputValidator) {
        String key = inputValidator.getProperty();
        List<InputValidator> inputValidators = this.inputValidators.get(key);
        if (inputValidators == null) {
            inputValidators = new ArrayList<InputValidator>();
        }
        if (!inputValidators.contains(inputValidator)) {
            inputValidators.add(inputValidator);
            this.inputValidators.put(key, inputValidators);
        }
    }

    public void removeInputValidator(InputValidator inputValidator) {
        String key = inputValidator.getProperty();
        List<InputValidator> inputValidators = this.inputValidators.get(key);
        if (inputValidators == null) {
            inputValidators = new ArrayList<InputValidator>();
        }
        if (inputValidators.contains(inputValidator)) {
            inputValidators.remove(inputValidator);
            if (inputValidators.size() > 0) {
                this.inputValidators.put(key, inputValidators);
            } else {
                this.inputValidators.remove(inputValidator.getProperty());
            }
        }
    }

    public void addBusinessValidator(BusinessValidator businessValidator) {
        this.businessValidators.add(businessValidator);
    }

    public void removeBusinessValidator(BusinessValidator businessValidator) {
        this.businessValidators.remove(businessValidator);
    }

}
