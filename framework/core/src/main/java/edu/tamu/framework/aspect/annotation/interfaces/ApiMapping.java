package edu.tamu.framework.aspect.annotation.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMethod;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@RequestMapping
//@MessageMapping
public @interface ApiMapping {
	String[] value() default {};
	RequestMethod method() default RequestMethod.GET;
}
