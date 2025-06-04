package com.faang.postservice.validation.resource;

public interface ResourceValidator {

    void validateCountFilesPerPost(Long postId, int numberFiles);

    void validateExistenceByKey(String key);

    void validatePostAuthorAndResourceAuthor(Long postAuthorId, Long resourceUserId);
}
