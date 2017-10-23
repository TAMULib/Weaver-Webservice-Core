package edu.tamu.weaver.auth.proxy;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Service
public class RequestMappingHandlerProxy implements MappingHandlerProxy<RequestMappingInfo, HandlerMethod, HttpServletRequest> {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public Optional<HandlerMethod> getHandler(HttpServletRequest object) {
        Optional<HandlerMethod> handler = Optional.empty();
        try {
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(object);
            handler = Optional.of((HandlerMethod) handlerExecutionChain.getHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler;
    }

    @Override
    public Map<RequestMappingInfo, HandlerMethod> getAllHandlers() {
        return requestMappingHandlerMapping.getHandlerMethods();
    }

}
