package com.fp.common.config;

import com.fp.common.properties.JwtProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JwtProperties.class)
@AutoConfiguration
public class JwtAutoConfiguration {

}
