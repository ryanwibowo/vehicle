package org.infomedia.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidYearRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYearRange {
    String message() default "YearStart must be less than or equal to YearEnd";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
