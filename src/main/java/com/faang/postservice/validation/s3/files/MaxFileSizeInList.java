package com.faang.postservice.validation.s3.files;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxFileSizeInListValidator.class)
@Documented
public @interface MaxFileSizeInList {

    String message() default "{validation.validFileSize}";

    long value() default (10485760);  // 10 Mb

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}