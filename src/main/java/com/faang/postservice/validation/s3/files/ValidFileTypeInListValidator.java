package com.faang.postservice.validation.s3.files;

import com.faang.postservice.validation.s3.file.ValidFileType;
import com.faang.postservice.validation.utils.FileUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ValidFileTypeInListValidator implements
        ConstraintValidator<ValidFileTypeInList, List<MultipartFile>> {

    private String message;

    @Override
    public void initialize(ValidFileTypeInList constraint) {
        this.message = constraint.message();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null) {
            return true;
        }
        for (MultipartFile file : files) {
            if (!FileUtils.isValid(file)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                message)
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
        return true;
    }
}
