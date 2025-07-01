package com.fp.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

@ConfigurationProperties(prefix = "fp.aws.sqs")
@Validated
@Data
public class SqsProperties {
    /**
     * Whether SQS is enabled.
     */
    private boolean enabled = true;

    @NotBlank(message = "SQS region must not be blank")
    private String region;


    public Region getAwsRegion() {
        return Region.of(region);
    }

    @NotNull
    private QueueConfig emailQueue;
    @NotNull
    private QueueConfig followerNotificationQueue;
    @NotNull
    private QueueConfig deadLetterQueue;


    @Data
    @Validated
    public static class QueueConfig {
        private String queueName;
        @NotNull(message = "Queue URL must not be null")
        private String queueUrl;
        private int visibilityTimeout = 30; // in seconds
        private int messageRetentionPeriodDays = 4; // in days
        private int maxReceiveCount = 10; // for dead letter queue
        private boolean enabled = true;
    }
}
