
package edu.tamu.weaver.validation.controller;

import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.utility.EntityUtility;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

@RestController("wvrValidationsController")
@RequestMapping("/validations")
public class ValidationsController {

    // TODO: add logging

    protected static final String MODEL_VALIDATOR_FIELD = "modelValidator";

    @Value("${app.model.packages}")
    private String[] modelPackages;

    @RequestMapping("/{entityName}")
    public ApiResponse validations(@PathVariable String entityName) {

        ApiResponse response = new ApiResponse(INVALID);

        Class<?> clazz = null;
        Object model = null;
        Object validator = null;

        for (String packageName : modelPackages) {
            try {
                clazz = Class.forName(packageName + "." + entityName);
                break;
            } catch (ClassNotFoundException e) {
                // e.printStackTrace();
            }
        }

        if (clazz != null) {

            try {
                model = clazz.getConstructor().newInstance();
            } catch (Exception e) {
                // e.printStackTrace();
            }

            if (model != null) {

                Field field = EntityUtility.recursivelyFindField(model.getClass(), MODEL_VALIDATOR_FIELD);

                if (field != null) {

                    field.setAccessible(true);

                    try {
                        validator = field.get(model);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }

                    field.setAccessible(false);
                }
            }

            if (validator != null) {

                Map<String, Map<String, InputValidator>> validations = new HashMap<String, Map<String, InputValidator>>();

                for (Entry<String, List<InputValidator>> entry : ((BaseModelValidator) validator).getInputValidators().entrySet()) {
                    String key = entry.getKey();
                    List<InputValidator> inputValidators = entry.getValue();
                    Map<String, InputValidator> inputValidatorMap = new HashMap<String, InputValidator>();
                    inputValidators.forEach(inputValidator -> {
                        inputValidatorMap.put(inputValidator.getType().toString(), inputValidator);
                    });
                    validations.put(key, inputValidatorMap);
                }

                response = new ApiResponse(SUCCESS, validations);

            }

        }

        return response;
    }

}
