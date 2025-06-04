package com.faang.postservice.validation.s3.files;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

public class FileRequiredInListValidator implements
        ConstraintValidator<FileRequiredInList, List<MultipartFile>> {

    @Override
    public void initialize(FileRequiredInList constraint) {
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null) {
            return true;
        }
        for (MultipartFile file : files) {
            if (Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
