package org.rakhmonov.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "click")
@Data
public class ClickConfig {
    private String merchantId;
    private String serviceId;
    private String secretKey;
    private String prepareUrl;
    private String completeUrl;
    private String cancelUrl;
    private String baseUrl = "https://my.click.uz/services/pay";
    private String apiUrl = "https://api.click.uz/v2";
    private String webhookUrl;
    private String returnUrl;
    private String callbackUrl;
    private Integer timeout = 30000; // 30 seconds
    private Boolean testMode = true;
}

