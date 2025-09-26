package org.rakhmonov.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "click")
@Data
public class ClickConfig {
    private Integer merchantId;
    private Integer serviceId;
    private String merchantUserId;
    private String secretKey;
    private String apiUrl = "https://api.click.uz/v2/merchant";
    private String webhookUrl;
    private String returnUrl;
    private String callbackUrl;
    private Integer timeout = 30000; // 30 seconds
    private Boolean testMode = true;
}

