/* 
 * WebSocketRequestMappingHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.mapping;

/*
 * Copyright 2002-2016 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.annotation.support.AnnotationExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.annotation.support.DestinationVariableMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.HeaderMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.MessageMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageTypeMessageCondition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.PrincipalMethodArgumentResolver;
import org.springframework.messaging.simp.annotation.support.SendToMethodReturnValueHandler;
import org.springframework.messaging.simp.annotation.support.SubscriptionMethodReturnValueHandler;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.mapping.condition.WebSocketRequestCondition;
import edu.tamu.framework.mapping.info.CustomSimpMessageMappingInfo;
import edu.tamu.framework.mapping.support.CustomPayloadArgumentResolver;

public class WebSocketRequestMappingHandler extends CustomAbstractMethodMessageHandler<CustomSimpMessageMappingInfo> implements SmartLifecycle {

    private final SubscribableChannel clientInboundChannel;

    private final SimpMessageSendingOperations clientMessagingTemplate;

    private final SimpMessageSendingOperations brokerTemplate;

    private MessageConverter messageConverter;

    private ConversionService conversionService = new DefaultFormattingConversionService();

    private PathMatcher pathMatcher = new AntPathMatcher();

    private boolean slashPathSeparator = true;

    private Validator validator;

    private MessageHeaderInitializer headerInitializer;

    private final Object lifecycleMonitor = new Object();

    private volatile boolean running = false;

    public WebSocketRequestMappingHandler(SubscribableChannel clientInboundChannel, MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {

        Assert.notNull(clientInboundChannel, "clientInboundChannel must not be null");
        Assert.notNull(clientOutboundChannel, "clientOutboundChannel must not be null");
        Assert.notNull(brokerTemplate, "brokerTemplate must not be null");

        this.clientInboundChannel = clientInboundChannel;
        this.clientMessagingTemplate = new SimpMessagingTemplate(clientOutboundChannel);
        this.brokerTemplate = brokerTemplate;

        Collection<MessageConverter> messageConverters = new ArrayList<MessageConverter>();

        MappingJackson2MessageConverter jacksonConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        jacksonConverter.setObjectMapper(objectMapper);

        messageConverters.add(jacksonConverter);

        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.ALL);

        StringMessageConverter stringMessageConverter = new StringMessageConverter();
        stringMessageConverter.setContentTypeResolver(resolver);

        messageConverters.add(stringMessageConverter);

        messageConverters.add(new SimpleMessageConverter());
        messageConverters.add(new ByteArrayMessageConverter());

        this.messageConverter = new CompositeMessageConverter(messageConverters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        synchronized (this.lifecycleMonitor) {
            return this.running;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        synchronized (this.lifecycleMonitor) {
            this.clientInboundChannel.subscribe(this);
            this.running = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        synchronized (this.lifecycleMonitor) {
            this.running = false;
            this.clientInboundChannel.unsubscribe(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(Runnable callback) {
        synchronized (this.lifecycleMonitor) {
            stop();
            callback.run();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDestinationPrefixes(Collection<String> prefixes) {
        super.setDestinationPrefixes(appendSlashes(prefixes));
    }

    private static Collection<String> appendSlashes(Collection<String> prefixes) {
        if (CollectionUtils.isEmpty(prefixes)) {
            return prefixes;
        }
        Collection<String> result = new ArrayList<String>(prefixes.size());
        for (String prefix : prefixes) {
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            result.add(prefix);
        }
        return result;
    }

    public void setMessageConverter(MessageConverter converter) {
        this.messageConverter = converter;
    }

    public MessageConverter getMessageConverter() {
        return this.messageConverter;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ConversionService getConversionService() {
        return this.conversionService;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
        this.slashPathSeparator = this.pathMatcher.combine("a", "a").equals("a/a");
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public Validator getValidator() {
        return this.validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleMatch(CustomSimpMessageMappingInfo mapping, HandlerMethod handlerMethod, String lookupDestination, Message<?> message) {

        if ("SUBSCRIBE".equals(message.getHeaders().get("stompCommand").toString())) {
            return;
        }

        String matchedPattern = mapping.getDestinationConditions().getPatterns().iterator().next();

        Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchedPattern, lookupDestination);

        if (!CollectionUtils.isEmpty(vars)) {
            MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
            accessor.setHeader(DestinationVariableMethodArgumentResolver.DESTINATION_TEMPLATE_VARIABLES_HEADER, vars);
        }

        try {
            SimpAttributesContextHolder.setAttributesFromMessage(message);
            super.handleMatch(mapping, handlerMethod, lookupDestination, message);
        } finally {
            SimpAttributesContextHolder.resetAttributes();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(Class<?> beanType) {
        return new AnnotationExceptionHandlerMethodResolver(beanType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDestination(Message<?> message) {
        return SimpMessageHeaderAccessor.getDestination(message.getHeaders());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getDirectLookupDestinations(CustomSimpMessageMappingInfo mapping) {
        Set<String> result = new LinkedHashSet<String>();
        for (String pattern : mapping.getDestinationConditions().getPatterns()) {
            if (!this.pathMatcher.isPattern(pattern)) {
                result.add(pattern);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLookupDestination(String destination) {
        if (destination == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(getDestinationPrefixes())) {
            return destination;
        }
        for (String prefix : getDestinationPrefixes()) {
            if (destination.startsWith(prefix)) {
                if (this.slashPathSeparator) {
                    return destination.substring(prefix.length() - 1);
                } else {
                    return destination.substring(prefix.length());
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Comparator<CustomSimpMessageMappingInfo> getMappingComparator(final Message<?> message) {
        return new Comparator<CustomSimpMessageMappingInfo>() {
            @Override
            public int compare(CustomSimpMessageMappingInfo info1, CustomSimpMessageMappingInfo info2) {
                return info1.compareTo(info2, message);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CustomSimpMessageMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        ApiMapping methodAnnotation = AnnotationUtils.findAnnotation(method, ApiMapping.class);

        ApiMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiMapping.class);

        if (methodAnnotation != null) {
            CustomSimpMessageMappingInfo result = createMessageMappingInfo(methodAnnotation);
            if (typeAnnotation != null) {
                result = createMessageMappingInfo(typeAnnotation).combine(result);
            }
            return result;
        }

        return null;
    }

    private CustomSimpMessageMappingInfo createMessageMappingInfo(ApiMapping annotation) {
        return new CustomSimpMessageMappingInfo(SimpMessageTypeMessageCondition.MESSAGE, new WebSocketRequestCondition(annotation.value(), this.pathMatcher));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CustomSimpMessageMappingInfo getMatchingMapping(CustomSimpMessageMappingInfo mapping, Message<?> message) {
        return mapping.getMatchingCondition(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<? extends HandlerMethodArgumentResolver> initArgumentResolvers() {
        ConfigurableBeanFactory beanFactory = (ClassUtils.isAssignableValue(ConfigurableApplicationContext.class, getApplicationContext())) ? ((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory() : null;

        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

        // Annotation-based argument resolution
        resolvers.add(new HeaderMethodArgumentResolver(this.conversionService, beanFactory));
        resolvers.add(new HeadersMethodArgumentResolver());
        resolvers.add(new DestinationVariableMethodArgumentResolver(this.conversionService));

        // Type-based argument resolution
        resolvers.add(new PrincipalMethodArgumentResolver());
        resolvers.add(new MessageMethodArgumentResolver());

        resolvers.addAll(getCustomArgumentResolvers());
        resolvers.add(new CustomPayloadArgumentResolver(this.messageConverter, this.validator));

        return resolvers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

        // Annotation-based return value types
        SendToMethodReturnValueHandler sth = new SendToMethodReturnValueHandler(this.brokerTemplate, true);
        sth.setHeaderInitializer(this.headerInitializer);
        handlers.add(sth);

        SubscriptionMethodReturnValueHandler sh = new SubscriptionMethodReturnValueHandler(this.clientMessagingTemplate);
        sh.setHeaderInitializer(this.headerInitializer);
        handlers.add(sh);

        // custom return value types
        handlers.addAll(getCustomReturnValueHandlers());

        // catch-all
        sth = new SendToMethodReturnValueHandler(this.brokerTemplate, false);
        sth.setHeaderInitializer(this.headerInitializer);
        handlers.add(sth);

        return handlers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanType);
        for (Method method : methods) {
            if (AnnotationUtils.findAnnotation(method, ApiMapping.class) != null) {
                return true;
            }
        }
        return false;
    }

}