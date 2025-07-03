package com.fp.lambda.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationConfig {
    private LoggingConfig logging;
    private LambdaConfig lambda;
    private ServicesConfig services;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServicesConfig{
        private String accountUrl = "http://localhost:8080";
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoggingConfig {
        private String level = "INFO";
        private String rootLevel = "WARN";
        private Map<String, String> packageLevels;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LambdaConfig{
        private SesConfig ses;
        private SqsConfig sqs;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SesConfig {
            private String fromEmail;
            private String fromName;
            private String region;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SqsConfig {
            private String region;
            private Map<String, String> queues;
        }



    }

    public void validate() {
        if (logging == null) {
            throw new IllegalStateException("Logging configuration is required");
        }

        if (lambda.ses == null) {
            throw new IllegalStateException("SES configuration is required");
        }

        if (lambda.ses.fromEmail == null || lambda.ses.fromEmail.isEmpty()) {
            throw new IllegalStateException("SES from email is required");
        }
        // 验证日志级别
        validateLogLevel(logging.level);
        validateLogLevel(logging.rootLevel);
    }

    private void validateLogLevel(String level) {
        if (level != null && !Arrays.asList("DEBUG", "INFO", "WARN", "ERROR")
                .contains(level.toUpperCase())) {
            throw new IllegalArgumentException("Invalid log level: " + level);
        }
    }
    public static ApplicationConfig load() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ApplicationConfig applicationConfig = mapper.readValue(
                    ApplicationConfig.class.getClassLoader().getResourceAsStream("application.yml"),
                    ApplicationConfig.class
            );

            // Resolve environment variables in the configuration
            return resolveEnvironmentVariables(applicationConfig);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.yml", e);
        }
    }

    private static ApplicationConfig resolveEnvironmentVariables(ApplicationConfig config) {
        if (config.logging != null){
            if (config.logging != null) {
                config.logging.level = resolveProperty(config.logging.level);
                config.logging.rootLevel = resolveProperty(config.logging.rootLevel);
                // 处理包级别日志配置
                if (config.logging.packageLevels != null) {
                    config.logging.packageLevels.replaceAll((key, value) -> resolveProperty(value));
                }
            }
        }
        if (config.lambda != null) {
            // 处理SES配置
            if (config.lambda.ses != null) {
                config.lambda.ses.fromEmail = resolveProperty(config.lambda.ses.fromEmail);
                config.lambda.ses.fromName = resolveProperty(config.lambda.ses.fromName);
            }

            // 处理SQS配置
            if (config.lambda.sqs != null) {
                if (config.lambda.sqs.queues != null) {
                    config.lambda.sqs.queues.replaceAll((key, value) -> resolveProperty(value));
                }
            }
        }

        // 处理Services配置
        if (config.services != null) {
            config.services.accountUrl = resolveProperty(config.services.accountUrl);
        }

        return config;
    }

    private static String resolveProperty(String value) {
        if (value == null) return null;

        // 处理 ${ENV_VAR} 或 ${ENV_VAR:default} 格式
        if (value.startsWith("${") && value.endsWith("}")) {
            String propertyExpr = value.substring(2, value.length() - 1);
            String[] parts = propertyExpr.split(":", 2);
            String envVar = parts[0];
            String defaultValue = parts.length > 1 ? parts[1] : null;

            String envValue = System.getenv(envVar);

            if (envValue != null) {
                return envValue;
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                return value; // 返回原值
            }
        }

        return value;
    }
}
