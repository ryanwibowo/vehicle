package org.infomedia.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

public class YearValidator implements ConstraintValidator<ValidYear, Integer> {

    /**
     * Validate the given makeYear is not in the future.
     *
     * @param makeYear the year to validate
     * @param context  the {@link ConstraintValidatorContext} used to build violation messages
     * @return {@code true} if makeYear is null or less than or equal to the current year; {@code false} otherwise
     */
    @Override
    public boolean isValid(Integer makeYear, ConstraintValidatorContext context) {
        if (makeYear == null) {
            return true;
        }
        int currentYear = Year.now().getValue();
        return makeYear <= currentYear;
    }
}
