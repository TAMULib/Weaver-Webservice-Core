package edu.tamu.weaver.wro.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import edu.tamu.weaver.wro.manager.factory.CustomConfigurableWroManagerFactory;
import edu.tamu.weaver.wro.service.ThemeManagerService;
import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.handler.factory.SimpleRequestHandlerFactory;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import wro4j.http.handler.CustomRequestHandler;

@Configuration
@EnableJpaRepositories(basePackages = { "edu.tamu.weaver.theme.model.repo" })
@EntityScan(basePackages = { "edu.tamu.weaver.theme.model" })
public class WroConfiguration {

    @Lazy
    @Autowired
    private ThemeManagerService themeManagerService;

    @Bean
    public FilterRegistrationBean webResourceOptimizer(Environment env) {
        FilterRegistrationBean fr = new FilterRegistrationBean();
        ConfigurableWroFilter filter = new ConfigurableWroFilter();
        Properties props = buildWroProperties(env);
        filter.setProperties(props);
        filter.setWroManagerFactory(new CustomConfigurableWroManagerFactory(props, themeManagerService));
        filter.setRequestHandlerFactory(new SimpleRequestHandlerFactory().addHandler(new CustomRequestHandler()));
        filter.setProperties(props);
        fr.setFilter(filter);
        fr.addUrlPatterns("/wro/*");
        return fr;
    }

    private static final String[] OTHER_WRO_PROP = new String[] { ConfigurableProcessorsFactory.PARAM_PRE_PROCESSORS, ConfigurableProcessorsFactory.PARAM_POST_PROCESSORS };

    private Properties buildWroProperties(Environment env) {
        Properties prop = new Properties();
        for (ConfigConstants c : ConfigConstants.values()) {
            addProperty(env, prop, c.name());
        }
        for (String name : OTHER_WRO_PROP) {
            addProperty(env, prop, name);
        }
        return prop;
    }

    private void addProperty(Environment env, Properties to, String name) {
        String value = env.getProperty("wro." + name);
        if (value != null) {
            to.put(name, value);
        }
    }

}
