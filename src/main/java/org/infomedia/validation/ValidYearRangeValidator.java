package org.infomedia.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.infomedia.dto.OperationRequest;

public class ValidYearRangeValidator implements ConstraintValidator<ValidYearRange, OperationRequest> {
    /**
     * Validate yearStart is less than or equal to yearEnd.
     *
     * @param request the {@link OperationRequest} to validate
     * @param context the {@link ConstraintValidatorContext} used to build violation messages
     * @return {@code true} if the years are valid or null; {@code false} if yearStart > yearEnd
     */
    @Override
    public boolean isValid(OperationRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;
        if (request.getYearStart() == null || request.getYearEnd() == null) return true;
        return request.getYearStart() <= request.getYearEnd();
    }
}
