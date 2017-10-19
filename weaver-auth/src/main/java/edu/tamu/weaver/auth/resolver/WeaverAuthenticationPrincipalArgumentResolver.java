package edu.tamu.weaver.auth.resolver;

/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.user.model.AbstractWeaverUser;

/**
 * Allows resolving the {@link Authentication#getPrincipal()} using the {@link AuthenticationPrincipal} annotation. For example, the following {@link Controller}:
 *
 * <pre>
 * &#64;Controller
 * public class MyController {
 *     &#64;MessageMapping("/im")
 *     public void im(@AuthenticationPrincipal CustomUser customUser) {
 *         // do something with CustomUser
 *     }
 * }
 * </pre>
 *
 * <p>
 * Will resolve the CustomUser argument using {@link Authentication#getPrincipal()} from the {@link SecurityContextHolder}. If the {@link Authentication} or {@link Authentication#getPrincipal()} is null, it will return null. If the types do not
 * match, null will be returned unless {@link AuthenticationPrincipal#errorOnInvalidType()} is true in which case a {@link ClassCastException} will be thrown.
 *
 * <p>
 * Alternatively, users can create a custom meta annotation as shown below:
 *
 * <pre>
 * &#064;Target({ ElementType.PARAMETER })
 * &#064;Retention(RetentionPolicy.RUNTIME)
 * &#064;AuthenticationPrincipal
 * public @interface CurrentUser {
 * }
 * </pre>
 *
 * <p>
 * The custom annotation can then be used instead. For example:
 *
 * <pre>
 * &#64;Controller
 * public class MyController {
 *     &#64;MessageMapping("/im")
 *     public void im(@CurrentUser CustomUser customUser) {
 *         // do something with CustomUser
 *     }
 * }
 * </pre>
 *
 * @author Rob Winch
 * @since 4.0
 */
public final class WeaverAuthenticationPrincipalArgumentResolver<U extends AbstractWeaverUser, R extends AbstractWeaverUserRepo<U>> implements HandlerMethodArgumentResolver {

    private ExpressionParser parser = new SpelExpressionParser();

    private BeanResolver beanResolver;

    private R userRepo;

    public WeaverAuthenticationPrincipalArgumentResolver(R userRepo) {
        this.userRepo = userRepo;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver# supportsParameter (org.springframework.core.MethodParameter)
     */
    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(AuthenticationPrincipal.class, parameter) != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver# resolveArgument (org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer,
     * org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        AuthenticationPrincipal authPrincipal = findMethodAnnotation(AuthenticationPrincipal.class, parameter);

        String expressionToParse = authPrincipal.expression();
        if (StringUtils.hasLength(expressionToParse)) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setRootObject(principal);
            context.setVariable("this", principal);
            context.setBeanResolver(beanResolver);

            Expression expression = this.parser.parseExpression(expressionToParse);
            principal = expression.getValue(context);
        }

        if (principal != null && !parameter.getParameterType().isAssignableFrom(principal.getClass())) {

            if (authPrincipal.errorOnInvalidType()) {
                throw new ClassCastException(principal + " is not assignable to " + parameter.getParameterType());
            } else {
                return null;
            }
        }
        Optional<U> user = userRepo.findByUsername(authentication.getName());
        if (user.isPresent()) {
            principal = user.get();
        }
        return principal;
    }

    /**
     * Sets the {@link BeanResolver} to be used on the expressions
     * 
     * @param beanResolver
     *            the {@link BeanResolver} to use
     */
    public void setBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }

    /**
     * Obtains the specified {@link Annotation} on the specified {@link MethodParameter}.
     *
     * @param annotationClass
     *            the class of the {@link Annotation} to find on the {@link MethodParameter}
     * @param parameter
     *            the {@link MethodParameter} to search for an {@link Annotation}
     * @return the {@link Annotation} that was found or null.
     */
    private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass, MethodParameter parameter) {
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