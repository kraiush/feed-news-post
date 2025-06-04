package com.faang.postservice.validation.resource;

import com.faang.postservice.config.file.AmazonS3Properties;
import com.faang.postservice.exception.DataValidationException;
import com.faang.postservice.exception.NotFoundException;
import com.faang.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceValidatorImpl implements ResourceValidator {

    private final ResourceRepository resourceRepository;
    private final AmazonS3Properties amazonS3Properties;

    @Override
    @Transactional
    public void validateCountFilesPerPost(Long postId, int filesToAdd) {
        if (resourceRepository.countAllByPost_Id(postId) + filesToAdd > amazonS3Properties.getMaxFilesAmount()) {
            log.error("Max files per post <{}> has been exceeded", amazonS3Properties.getMaxFilesAmount());
            throw new DataValidationException(String.format("Max files per post %s has been exceeded", amazonS3Properties.getMaxFilesAmount()));
        }
    }

    @Override
    @Transactional
    public void validateExistenceByKey(String key) {
        if (!resourceRepository.existsByKey(key)) {
            log.error("Resource with key <{}> has not been found", key);
            throw new NotFoundException(String.format("Resource with key $ has not been found", key));
        }
    }

    @Override
    public void validatePostAuthorAndResourceAuthor(Long postAuthorId, Long resourceUserId) {
        if (!postAuthorId.equals(resourceUserId)) {
            log.error("Mismatch post's authorId: <{}>  and resource's userId: <{}>", postAuthorId, resourceUserId);
            throw new NotFoundException("Mismatch post's authorId and resource's userId");
        }
    }
}
