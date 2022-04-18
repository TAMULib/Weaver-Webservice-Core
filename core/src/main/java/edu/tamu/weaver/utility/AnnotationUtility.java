package edu.tamu.weaver.utility;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;

public class AnnotationUtility {

    public static <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass, MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for (Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

}
