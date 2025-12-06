package software.netcore.radman.buisness.validation.constrain;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a string is a valid IPv4 or IPv6 address.
 * 
 * @since v. 1.0.4
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpAddressValidator.class)
@Documented
public @interface IpAddress {
    
    String message() default "Must be a valid IPv4 or IPv6 address";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow IPv4 addresses (default: true)
     */
    boolean ipv4() default true;
    
    /**
     * Allow IPv6 addresses (default: true)
     */
    boolean ipv6() default true;
}
