package com.fp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

///
/// # AWS SES configuration properties for email sending in account service.
///
///
@ConfigurationProperties(prefix = "fp.aws.ses")
@Data
public class SesProperties {
    /**
     * Enable or disable email functionality.
     */
    private boolean enabled;

    /**
     * AWS Region for SES
     */
    private String region;

    /**
     * Verified sender email address
     */
    private String fromEmail;

    /**
     * Sender Name
     */
    private String fromName;

    private String appBaseUrl;

    public Region getAwsRegion() {
        return Region.of(region);
    }

}

