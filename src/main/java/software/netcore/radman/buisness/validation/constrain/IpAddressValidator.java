package software.netcore.radman.buisness.validation.constrain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * Validator for IP address constraint.
 * 
 * @since v. 1.0.4
 */
public class IpAddressValidator implements ConstraintValidator<IpAddress, String> {

    private boolean ipv4Enabled;
    private boolean ipv6Enabled;

    @Override
    public void initialize(IpAddress constraintAnnotation) {
        this.ipv4Enabled = constraintAnnotation.ipv4();
        this.ipv6Enabled = constraintAnnotation.ipv6();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid - use @NotNull for null checking
        if (value == null || value.isEmpty()) {
            return true;
        }

        // Trim whitespace
        value = value.trim();

        InetAddressValidator validator = InetAddressValidator.getInstance();

        if (ipv4Enabled && ipv6Enabled) {
            return validator.isValid(value);
        } else if (ipv4Enabled) {
            return validator.isValidInet4Address(value);
        } else if (ipv6Enabled) {
            return validator.isValidInet6Address(value);
        }

        return false;
    }
}
