package com.faang.postservice.validation.s3.file;

import com.faang.postservice.validation.utils.FileUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeValidator implements ConstraintValidator<ValidFileType, MultipartFile> {

    private String message;

    @Override
    public void initialize(ValidFileType constraint) {
        this.message = constraint.message();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        if (!FileUtils.isValid(multipartFile)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            message)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}