package edu.tamu.weaver.messaging.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.jms.annotation.JmsListener;

@Documented
@JmsListener(destination = "")
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface WeaverMessageListener {

    @AliasFor(annotation = JmsListener.class, attribute = "destination")
    String destination() default "default";
}