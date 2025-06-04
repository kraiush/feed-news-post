package com.faang.postservice.validation.s3.files;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MaxFileSizeInListValidator implements
        ConstraintValidator<MaxFileSizeInList, List<MultipartFile>> {

    private MaxFileSizeInList constraint;

    @Override
    public void initialize(MaxFileSizeInList constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (constraint.value() <= 0 || files == null) {
            return true;
        }
        for (MultipartFile file: files) {
            if (file.getSize() > constraint.value()) {
                return false;
            }
        }
        return true;
    }
}


