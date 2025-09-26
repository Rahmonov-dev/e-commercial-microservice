package org.rakhmonov.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rakhmonov.paymentservice.config.ClickConfig;
import org.rakhmonov.paymentservice.dto.request.ClickPaymentRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * Create invoice with Click.uz API
     */
    public Map<String, Object> createInvoice(ClickPaymentRequest request, String phoneNumber) {
        log.info("Creating Click invoice for order: {}", request.getOrderId());
        
        String merchantTransId = "MERCHANT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("service_id", clickConfig.getServiceId());
        invoiceData.put("amount", request.getAmount());
        invoiceData.put("phone_number", phoneNumber);
        invoiceData.put("merchant_trans_id", merchantTransId);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(invoiceData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/invoice/create", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click invoice created successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to create Click invoice. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create Click invoice");
            }
        } catch (Exception e) {
            log.error("Error creating Click invoice", e);
            throw new RuntimeException("Error creating Click invoice: " + e.getMessage());
        }
    }

    /**
     * Check invoice status
     */
    public Map<String, Object> checkInvoiceStatus(Integer invoiceId) {
        log.info("Checking Click invoice status for ID: {}", invoiceId);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                clickConfig.getApiUrl() + "/invoice/status/" + clickConfig.getServiceId() + "/" + invoiceId,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click invoice status retrieved: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to check Click invoice status. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to check Click invoice status");
            }
        } catch (Exception e) {
            log.error("Error checking Click invoice status", e);
            throw new RuntimeException("Error checking Click invoice status: " + e.getMessage());
        }
    }

    /**
     * Check payment status
     */
    public Map<String, Object> checkPaymentStatus(Long paymentId) {
        log.info("Checking Click payment status for ID: {}", paymentId);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                clickConfig.getApiUrl() + "/payment/status/" + clickConfig.getServiceId() + "/" + paymentId,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click payment status retrieved: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to check Click payment status. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to check Click payment status");
            }
        } catch (Exception e) {
            log.error("Error checking Click payment status", e);
            throw new RuntimeException("Error checking Click payment status: " + e.getMessage());
        }
    }

    /**
     * Check payment status by merchant transaction ID
     */
    public Map<String, Object> checkPaymentStatusByMerchantTransId(String merchantTransId, String date) {
        log.info("Checking Click payment status by merchant trans ID: {}", merchantTransId);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                clickConfig.getApiUrl() + "/payment/status_by_mti/" + clickConfig.getServiceId() + "/" + merchantTransId + "/" + date,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click payment status by merchant trans ID retrieved: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to check Click payment status by merchant trans ID. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to check Click payment status by merchant trans ID");
            }
        } catch (Exception e) {
            log.error("Error checking Click payment status by merchant trans ID", e);
            throw new RuntimeException("Error checking Click payment status by merchant trans ID: " + e.getMessage());
        }
    }

    /**
     * Cancel payment (reversal)
     */
    public Map<String, Object> cancelPayment(Long paymentId) {
        log.info("Cancelling Click payment: {}", paymentId);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                clickConfig.getApiUrl() + "/payment/reversal/" + clickConfig.getServiceId() + "/" + paymentId,
                HttpMethod.DELETE,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
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
     * Create card token
     */
    public Map<String, Object> createCardToken(String cardNumber, String expireDate, Boolean temporary) {
        log.info("Creating Click card token");
        
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("service_id", clickConfig.getServiceId());
        tokenData.put("card_number", cardNumber);
        tokenData.put("expire_date", expireDate);
        tokenData.put("temporary", temporary ? 1 : 0);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(tokenData, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/card_token/request", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click card token created successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to create Click card token. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create Click card token");
            }
        } catch (Exception e) {
            log.error("Error creating Click card token", e);
            throw new RuntimeException("Error creating Click card token: " + e.getMessage());
        }
    }

    /**
     * Verify card token
     */
    public Map<String, Object> verifyCardToken(String cardToken, String smsCode) {
        log.info("Verifying Click card token");
        
        Map<String, Object> verifyData = new HashMap<>();
        verifyData.put("service_id", clickConfig.getServiceId());
        verifyData.put("card_token", cardToken);
        verifyData.put("sms_code", smsCode);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(verifyData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/card_token/verify", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click card token verified successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to verify Click card token. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to verify Click card token");
            }
        } catch (Exception e) {
            log.error("Error verifying Click card token", e);
            throw new RuntimeException("Error verifying Click card token: " + e.getMessage());
        }
    }

    /**
     * Payment with token
     */
    public Map<String, Object> paymentWithToken(String cardToken, BigDecimal amount, String merchantTransId) {
        log.info("Processing Click payment with token");
        
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("service_id", clickConfig.getServiceId());
        paymentData.put("card_token", cardToken);
        paymentData.put("amount", amount);
        paymentData.put("transaction_parameter", merchantTransId);
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clickConfig.getApiUrl() + "/card_token/payment", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click payment with token completed successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to process Click payment with token. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to process Click payment with token");
            }
        } catch (Exception e) {
            log.error("Error processing Click payment with token", e);
            throw new RuntimeException("Error processing Click payment with token: " + e.getMessage());
        }
    }

    /**
     * Delete card token
     */
    public Map<String, Object> deleteCardToken(String cardToken) {
        log.info("Deleting Click card token");
        
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                clickConfig.getApiUrl() + "/card_token/" + clickConfig.getServiceId() + "/" + cardToken,
                HttpMethod.DELETE,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                log.info("Click card token deleted successfully: {}", responseBody);
                return responseBody;
            } else {
                log.error("Failed to delete Click card token. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to delete Click card token");
            }
        } catch (Exception e) {
            log.error("Error deleting Click card token", e);
            throw new RuntimeException("Error deleting Click card token: " + e.getMessage());
        }
    }

    /**
     * Create authentication headers for Click.uz API
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        
        // Generate timestamp
        long timestamp = System.currentTimeMillis() / 1000;
        
        // Generate digest: sha1(timestamp + secret_key)
        String digest = generateDigest(timestamp + clickConfig.getSecretKey());
        
        // Create Auth header: merchant_user_id:digest:timestamp
        String authHeader = clickConfig.getMerchantUserId() + ":" + digest + ":" + timestamp;
        headers.set("Auth", authHeader);
        
        return headers;
    }

    /**
     * Generate SHA1 digest
     */
    private String generateDigest(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
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
            log.error("Error generating SHA1 digest", e);
            throw new RuntimeException("Error generating SHA1 digest", e);
        }
    }
}