package com.faang.postservice.validation.hashtag;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidatorSetHashtag.class)
@Documented
public @interface ValidSetHashtag {

    String message() default "{validation.validHashtag}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}