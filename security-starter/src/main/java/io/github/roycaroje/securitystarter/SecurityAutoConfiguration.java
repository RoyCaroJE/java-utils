package io.github.roycaroje.securitystarter;

import io.github.roycaroje.security.SecurityConfiguration;
import io.github.roycaroje.security.JwtProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@Import(SecurityConfiguration.class)
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityAutoConfiguration {

}

