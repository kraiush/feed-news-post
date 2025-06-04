package com.faang.postservice.validation.s3.file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class FileRequiredValidator implements ConstraintValidator<FileRequired, MultipartFile> {

    @Override
    public void initialize(FileRequired constraint) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        return multipartFile != null
                && Objects.requireNonNull(multipartFile.getOriginalFilename()).isEmpty();
    }
}