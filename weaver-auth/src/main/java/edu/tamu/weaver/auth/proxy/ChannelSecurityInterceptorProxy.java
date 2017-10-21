package edu.tamu.weaver.auth.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.messaging.access.intercept.ChannelSecurityInterceptor;
import org.springframework.stereotype.Service;

@Service
public class ChannelSecurityInterceptorProxy extends AbstractSecurityInterceptorProxy {

    private ChannelSecurityInterceptor channelSecurityInterceptor;

    @Autowired
    public ChannelSecurityInterceptorProxy(ChannelSecurityInterceptor channelSecurityInterceptor) {
        this.channelSecurityInterceptor = channelSecurityInterceptor;
    }

    @Override
    public AccessDecisionManager getAccessDecisionManager() {
        return channelSecurityInterceptor.getAccessDecisionManager();
    }

    @Override
    public SecurityMetadataSource getSecurityMetadataSource() {
        return channelSecurityInterceptor.obtainSecurityMetadataSource();
    }

}
