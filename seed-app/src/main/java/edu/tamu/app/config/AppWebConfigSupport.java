package edu.tamu.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import edu.tamu.app.controller.interceptor.AppRestInterceptor;
import edu.tamu.framework.config.CoreWebConfigSupport;

@Configuration
public class AppWebConfigSupport extends CoreWebConfigSupport {
    
    @Autowired
    private AppRestInterceptor appRestInterceptor;
    
    @Override
    public Object getRestInterceptor() {
        return appRestInterceptor;
    }

}