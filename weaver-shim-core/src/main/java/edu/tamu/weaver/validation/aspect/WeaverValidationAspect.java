package edu.tamu.weaver.validation.aspect;

import static edu.tamu.weaver.response.ApiStatus.INVALID;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import edu.tamu.weaver.data.model.WeaverEntity;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.utility.EntityUtility;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;
import edu.tamu.weaver.validation.model.ValidatingEntity;
import edu.tamu.weaver.validation.results.ValidationResults;
import edu.tamu.weaver.validation.utility.ValidationUtility;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.BusinessValidator;
import edu.tamu.weaver.validation.validators.MethodValidator;

@Aspect
@Component
public class WeaverValidationAspect {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Around(value = "execution(* *(..)) && @annotation(edu.tamu.weaver.validation.aspect.annotation.WeaverValidation) && !@annotation(edu.tamu.framework.aspect.annotation.ApiMapping)")
    public ApiResponse validate(ProceedingJoinPoint joinPoint) throws Throwable {
        ApiResponse response;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        Object[] arguments = joinPoint.getArgs();

        ValidationResults validationResults = getParamaterValidationResults(method, arguments);

        ValidationUtility.aggregateValidationResults(validationResults, validateMethod(method, arguments));

        if (validationResults.isValid()) {
            response = (ApiResponse) joinPoint.proceed(arguments);
        } else {
            response = new ApiResponse(INVALID, validationResults);
        }

        return response;
    }

    private ValidationResults getParamaterValidationResults(Method method, Object[] arguments) {
        ValidationResults validationResults = new ValidationResults();
        int index = 0;
        boolean validated = false;
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof WeaverValidatedModel) {
                    validationResults = validateModel((ValidatingEntity) arguments[index], method);
                    validated = true;
                    break;
                }
            }
            if (validated) {
                break;
            }
            index++;
        }
        return validationResults;
    }

    @SuppressWarnings("unused")
    private TransactionTemplate createTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        return transactionTemplate;
    }

    @SuppressWarnings("unused")
    private Object ensureCompleteModel(Object model) {
        if (model != null) {
            List<String> serializedProperties = EntityUtility.recursivelyFindJsonIdentityReference(model.getClass());
            if (serializedProperties.size() > 0) {
                List<Object> response = EntityUtility.queryWithClassById(model.getClass(), ((WeaverEntity) model).getId());
                if (response.size() > 0) {
                    Object fullModel = response.get(0);

                    serializedProperties.forEach(serializedProperty -> {
                        EntityUtility.setValueForProperty(model, serializedProperty, EntityUtility.getValueForProperty(fullModel, serializedProperty));
                    });
                }
            }
        }
        return model;
    }

    public <V extends ValidatingEntity> ValidationResults validateModel(V model, Method method) {
        for (Annotation validationAnnotation : method.getAnnotations()) {
            if (validationAnnotation instanceof WeaverValidation) {
                for (WeaverValidation.Business businessAnnotation : ((WeaverValidation) validationAnnotation).business()) {
                    ((BaseModelValidator) ((ValidatingEntity) model).getModelValidator()).addBusinessValidator(new BusinessValidator(businessAnnotation.value(), businessAnnotation.joins(), businessAnnotation.params(), businessAnnotation.path(), businessAnnotation.restrict()));
                }
            }
        }
        return ((ValidatingEntity) model).validate((ValidatingEntity) model);
    }

    public ValidationResults validateMethod(Method method, Object[] args) {
        ValidationResults validationResults = new ValidationResults();
        for (Annotation validationAnnotation : method.getAnnotations()) {
            if (validationAnnotation instanceof WeaverValidation) {
                for (WeaverValidation.Method methodAnnotation : ((WeaverValidation) validationAnnotation).method()) {
                    ValidationUtility.aggregateValidationResults(validationResults, ValidationUtility.validateMethod(new MethodValidator(methodAnnotation.value(), methodAnnotation.model(), methodAnnotation.params(), args)));
                }
            }
        }
        return validationResults;
    }

}
