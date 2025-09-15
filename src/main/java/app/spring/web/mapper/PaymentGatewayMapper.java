package app.spring.web.mapper;

import app.spring.web.model.PaymentGateway;
import tk.mybatis.mapper.common.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Payment Gateway Mapper for database operations
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@org.apache.ibatis.annotations.Mapper
public interface PaymentGatewayMapper extends Mapper<PaymentGateway> {
    
    /**
     * Find gateways by active status
     */
    @Select("SELECT * FROM crm_payment_gateway WHERE is_active = #{isActive} ORDER BY gateway_name")
    List<PaymentGateway> findByIsActive(@Param("isActive") Boolean isActive);
    
    /**
     * Find gateway by name
     */
    @Select("SELECT * FROM crm_payment_gateway WHERE gateway_name = #{gatewayName}")
    PaymentGateway findByGatewayName(@Param("gatewayName") String gatewayName);
    
    /**
     * Find gateways by type
     */
    @Select("SELECT * FROM crm_payment_gateway WHERE gateway_type = #{gatewayType} AND is_active = true ORDER BY gateway_name")
    List<PaymentGateway> findByGatewayType(@Param("gatewayType") String gatewayType);
    
    /**
     * Find gateways supporting currency
     */
    @Select("SELECT * FROM crm_payment_gateway WHERE supported_currencies LIKE CONCAT('%', #{currency}, '%') AND is_active = true")
    List<PaymentGateway> findBySupportedCurrency(@Param("currency") String currency);
}
