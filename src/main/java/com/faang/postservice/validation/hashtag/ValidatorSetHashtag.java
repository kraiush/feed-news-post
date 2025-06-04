package com.faang.postservice.validation.hashtag;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidatorSetHashtag implements ConstraintValidator<ValidSetHashtag, Set<String>> {

    private static final String HASHTAG_PATTERN = "^#[A-Za-z0-9_]+$";

    @Override
    public void initialize(ValidSetHashtag constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Set<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String hashtag : value) {
            if (!hashtag.matches(HASHTAG_PATTERN)) {
                return false;
            }
        }
        return true;
    }
}
