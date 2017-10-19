package edu.tamu.weaver.user.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = { "edu.tamu.weaver.user.model" })
public class UserConfiguration {

}
