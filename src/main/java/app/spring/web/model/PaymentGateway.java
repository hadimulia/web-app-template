package app.spring.web.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payment Gateway entity representing different payment processors
 * Supports multiple payment gateways like Midtrans, Stripe, PayPal, etc.
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "crm_payment_gateway")
public class PaymentGateway {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gateway_name", nullable = false, unique = true, length = 50)
    private String gatewayName;
    
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;
    
    @Column(name = "gateway_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private GatewayType gatewayType;
    
    @Column(name = "api_key", length = 255)
    private String apiKey;
    
    @Column(name = "secret_key", length = 255)
    private String secretKey;
    
    @Column(name = "merchant_id", length = 100)
    private String merchantId;
    
    @Column(name = "environment", length = 10)
    @Enumerated(EnumType.STRING)
    private Environment environment = Environment.SANDBOX;
    
    @Column(name = "webhook_url", length = 255)
    private String webhookUrl;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "supported_currencies", length = 50)
    private String supportedCurrencies = "IDR";
    
    @Column(name = "fee_percent", precision = 5, scale = 2)
    private Double feePercent = 0.0;
    
    @Column(name = "fee_fixed", precision = 10, scale = 2)
    private Double feeFixed = 0.0;
    
    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum GatewayType {
        BANK, EWALLET, CARD, VA, QR, CRYPTO
    }
    
    public enum Environment {
        SANDBOX, PRODUCTION
    }
    
    // Constructors
    public PaymentGateway() {}
    
    public PaymentGateway(String gatewayName, String displayName, GatewayType gatewayType) {
        this.gatewayName = gatewayName;
        this.displayName = displayName;
        this.gatewayType = gatewayType;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isProduction() {
        return Environment.PRODUCTION.equals(this.environment);
    }
    
    public boolean isSandbox() {
        return Environment.SANDBOX.equals(this.environment);
    }
    
    public boolean supportsMultipleCurrencies() {
        return supportedCurrencies != null && supportedCurrencies.contains(",");
    }
    
    public String[] getSupportedCurrencyArray() {
        if (supportedCurrencies == null) return new String[0];
        return supportedCurrencies.split(",");
    }
    
    public boolean supportsCurrency(String currency) {
        if (supportedCurrencies == null || currency == null) return false;
        return supportedCurrencies.toLowerCase().contains(currency.toLowerCase());
    }
    
    public Double calculateFee(Double amount) {
        if (amount == null || amount <= 0) return 0.0;
        
        Double percentFee = (feePercent != null) ? (amount * feePercent / 100.0) : 0.0;
        Double fixedFee = (feeFixed != null) ? feeFixed : 0.0;
        
        return percentFee + fixedFee;
    }
    
    public Double getNetAmount(Double grossAmount) {
        if (grossAmount == null || grossAmount <= 0) return 0.0;
        return grossAmount - calculateFee(grossAmount);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGatewayName() {
        return gatewayName;
    }
    
    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public GatewayType getGatewayType() {
        return gatewayType;
    }
    
    public void setGatewayType(GatewayType gatewayType) {
        this.gatewayType = gatewayType;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    
    public String getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    public String getWebhookUrl() {
        return webhookUrl;
    }
    
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getSupportedCurrencies() {
        return supportedCurrencies;
    }
    
    public void setSupportedCurrencies(String supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }
    
    public Double getFeePercent() {
        return feePercent;
    }
    
    public void setFeePercent(Double feePercent) {
        this.feePercent = feePercent;
    }
    
    public Double getFeeFixed() {
        return feeFixed;
    }
    
    public void setFeeFixed(Double feeFixed) {
        this.feeFixed = feeFixed;
    }
    
    public String getConfigJson() {
        return configJson;
    }
    
    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentGateway that = (PaymentGateway) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(gatewayName, that.gatewayName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, gatewayName);
    }
    
    @Override
    public String toString() {
        return "PaymentGateway{" +
                "id=" + id +
                ", gatewayName='" + gatewayName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", gatewayType=" + gatewayType +
                ", environment=" + environment +
                ", isActive=" + isActive +
                ", supportedCurrencies='" + supportedCurrencies + '\'' +
                ", feePercent=" + feePercent +
                ", feeFixed=" + feeFixed +
                '}';
    }
}
