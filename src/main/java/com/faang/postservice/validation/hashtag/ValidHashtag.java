package com.faang.postservice.validation.hashtag;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy= ValidatorHashtag.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidHashtag {

    String message() default "{validation.validHashtag}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}



