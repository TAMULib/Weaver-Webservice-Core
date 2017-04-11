/* 
 * ApiValidation.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.aspect.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.tamu.framework.enums.BusinessValidationType;
import edu.tamu.framework.enums.MethodValidationType;

@Target({ METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiValidation {

    Business[] business() default {};

    Method[] method() default {};

    @Target({ METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Business {

        BusinessValidationType value();

        Class<?>[] joins() default {};

        String[] params() default {};

        String[] path() default {};

        String restrict() default "";
    }

    @Target({ METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Method {

        MethodValidationType value();

        Class<?> model();

        String[] params() default {};
    }
}
