package com.faang.postservice.service.resource;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.faang.postservice.dto.resource.ResourceDto;
import com.faang.postservice.exception.NotFoundException;
import com.faang.postservice.exception.S3Exception;
import com.faang.postservice.mapper.dto.ResourceMapper;
import com.faang.postservice.model.Post;
import com.faang.postservice.model.Resource;
import com.faang.postservice.repository.ResourceRepository;
import com.faang.postservice.service.file.AmazonS3Service;
import com.faang.postservice.service.post.PostService;
import com.faang.postservice.validation.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final AmazonS3Service amazonS3Service;
    private final PostService postService;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;

    @Override
    @Transactional
    public Resource findById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Resource with Id: %s was not found", id)));
    }

    @Override
    @Transactional
    public List<ResourceDto> create(Long postId, Long userId, List<MultipartFile> files) {
        Post post = postService.getById(postId);
        resourceValidator.validatePostAuthorAndResourceAuthor(post.getAuthorId(), userId);
        resourceValidator.validateCountFilesPerPost(postId, files.size());

        ExecutorService executorService = Executors.newFixedThreadPool(files.size());
        try (Closeable ignored = executorService::shutdown) {
            List<CompletableFuture<Resource>> resources = new ArrayList<>();
            List<Resource> savedResources = new ArrayList<>();

            files.forEach(file -> {
                CompletableFuture<Resource> resource = CompletableFuture.supplyAsync(() -> {
                    String key = amazonS3Service.uploadFile(file, userId);
                    return Resource.builder()
                            .name(file.getOriginalFilename())
                            .key(key)
                            .size(file.getSize())
                            .type(file.getContentType())
                            .post(post)
                            .build();
                }, executorService);
                resources.add(resource);
            });

            resources.forEach(resource -> {
                Resource resourceToSave = resource.join();
                savedResources.add(resourceRepository.save(resourceToSave));
            });

            log.info("Successfully create resource(s)");
            return savedResources.stream()
                    .map(resourceMapper::toDto)
                    .toList();

        } catch (AmazonS3Exception | IOException ex) {
            log.error(ex.getMessage());
            throw new S3Exception(ex.getMessage());
        }
    }

    @Override
    public InputStream download(String key, Long userId) {
        resourceValidator.validateExistenceByKey(key);
        return amazonS3Service.downloadFile(key, userId);
    }

    @Override
    @Transactional
    public void delete(String key, Long userId) {
        Resource resourceToRemove = resourceRepository.findByKey(key);
        Post post = resourceToRemove.getPost();
        resourceValidator.validatePostAuthorAndResourceAuthor(post.getAuthorId(), userId);
        resourceValidator.validateExistenceByKey(key);
        resourceRepository.deleteByKey(key);
        amazonS3Service.deleteFile(key, userId);
        log.error("Successfully delete file from resources");
    }
}
