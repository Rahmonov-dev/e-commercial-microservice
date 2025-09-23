package org.rakhmonov.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.config.ClickConfig;
import org.rakhmonov.paymentservice.dto.request.ClickPaymentRequest;
import org.rakhmonov.paymentservice.entity.ClickPayment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickApiService {
    private final ClickConfig clickConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Prepare payment with Click.uz
     */
    public Map<String, Object> preparePayment(ClickPaymentRequest request) {
        log.info("Preparing Click payment for order: {}", request.getOrderId());
        
        String merchantTransId = "MERCHANT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String signString = generateSignString(merchantTransId, request.getAmount(), request.getCurrency());
        
        Map<String, Object> prepareData = new HashMap<>();
        prepareData.put("service_id", clickConfig.getServiceId());
        prepareData.put("merchant_trans_id", merchantTransId);
        prepareData.put("amount", request.getAmount());
        prepareData.put("currency", request.getCurrency());
        prepareData.put("action", "prepare");
        prepareData.put("sign_string", signString);
        prepareData.put("sign_time", String.valueOf(System.currentTimeMillis()));
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(prepareData, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/payment/prepare", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                log.info("Click payment prepared successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to prepare Click payment. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to prepare Click payment");
            }
        } catch (Exception e) {
            log.error("Error preparing Click payment", e);
            throw new RuntimeException("Error preparing Click payment: " + e.getMessage());
        }
    }

    /**
     * Complete payment with Click.uz
     */
    public Map<String, Object> completePayment(String clickTransId, String merchantTransId, 
                                               BigDecimal amount, String currency) {
        log.info("Completing Click payment: {}", clickTransId);
        
        String signString = generateSignString(merchantTransId, amount, currency);
        
        Map<String, Object> completeData = new HashMap<>();
        completeData.put("service_id", clickConfig.getServiceId());
        completeData.put("click_trans_id", clickTransId);
        completeData.put("merchant_trans_id", merchantTransId);
        completeData.put("amount", amount);
        completeData.put("currency", currency);
        completeData.put("action", "complete");
        completeData.put("sign_string", signString);
        completeData.put("sign_time", String.valueOf(System.currentTimeMillis()));
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(completeData, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/payment/complete", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                log.info("Click payment completed successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to complete Click payment. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to complete Click payment");
            }
        } catch (Exception e) {
            log.error("Error completing Click payment", e);
            throw new RuntimeException("Error completing Click payment: " + e.getMessage());
        }
    }

    /**
     * Cancel payment with Click.uz
     */
    public Map<String, Object> cancelPayment(String clickTransId, String merchantTransId, 
                                             BigDecimal amount, String currency) {
        log.info("Cancelling Click payment: {}", clickTransId);
        
        String signString = generateSignString(merchantTransId, amount, currency);
        
        Map<String, Object> cancelData = new HashMap<>();
        cancelData.put("service_id", clickConfig.getServiceId());
        cancelData.put("click_trans_id", clickTransId);
        cancelData.put("merchant_trans_id", merchantTransId);
        cancelData.put("amount", amount);
        cancelData.put("currency", currency);
        cancelData.put("action", "cancel");
        cancelData.put("sign_string", signString);
        cancelData.put("sign_time", String.valueOf(System.currentTimeMillis()));
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(cancelData, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/payment/cancel", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                log.info("Click payment cancelled successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to cancel Click payment. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to cancel Click payment");
            }
        } catch (Exception e) {
            log.error("Error cancelling Click payment", e);
            throw new RuntimeException("Error cancelling Click payment: " + e.getMessage());
        }
    }

    /**
     * Generate payment URL for redirect
     */
    public String generatePaymentUrl(String clickTransId, BigDecimal amount, String currency, 
                                   String description, Long orderId) {
        log.info("Generating Click payment URL for order: {}", orderId);
        
        Map<String, String> params = new HashMap<>();
        params.put("service_id", clickConfig.getServiceId());
        params.put("merchant_id", clickConfig.getMerchantId());
        params.put("amount", amount.toString());
        params.put("currency", currency);
        params.put("description", description);
        params.put("click_trans_id", clickTransId);
        params.put("return_url", clickConfig.getReturnUrl());
        params.put("callback_url", clickConfig.getCallbackUrl());
        
        StringBuilder urlBuilder = new StringBuilder(clickConfig.getBaseUrl());
        urlBuilder.append("?");
        
        params.forEach((key, value) -> {
            urlBuilder.append(key).append("=").append(value).append("&");
        });
        
        String paymentUrl = urlBuilder.toString();
        log.info("Generated Click payment URL: {}", paymentUrl);
        
        return paymentUrl;
    }

    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(Map<String, String> webhookData) {
        try {
            String receivedSign = webhookData.get("sign_string");
            String merchantTransId = webhookData.get("merchant_trans_id");
            String amount = webhookData.get("amount");
            String currency = webhookData.get("currency");
            
            String expectedSign = generateSignString(merchantTransId, new BigDecimal(amount), currency);
            
            boolean isValid = receivedSign.equals(expectedSign);
            log.info("Webhook signature verification: {}", isValid);
            
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    /**
     * Generate sign string for Click.uz API
     */
    private String generateSignString(String merchantTransId, BigDecimal amount, String currency) {
        try {
            String data = merchantTransId + amount + currency + clickConfig.getSecretKey();
            MessageDigest md = MessageDigest.getInstance("SHA256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating sign string", e);
            throw new RuntimeException("Error generating sign string", e);
        }
    }
}

