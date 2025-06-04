package com.faang.postservice.service.resource;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.faang.postservice.dto.resource.ResourceDto;
import com.faang.postservice.mapper.dto.ResourceMapper;
import com.faang.postservice.model.Post;
import com.faang.postservice.model.Resource;
import com.faang.postservice.repository.ResourceRepository;
import com.faang.postservice.service.file.AmazonS3Service;
import com.faang.postservice.service.post.PostService;
import com.faang.postservice.validation.resource.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Spy
    private ResourceMapper resourceMapper;
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private PostService postService;

    @InjectMocks
    private ResourceServiceImpl resourceServiceImpl;

    private final long userId = new Random().nextLong(1, 500);
    private final String key = UUID.randomUUID().toString();

    @Test
    void successFindById() {
        long resourceId = new Random().nextLong(1, 10000);
        Resource resource = Resource.builder().id(resourceId).build();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));
        Resource result = resourceServiceImpl.findById(resourceId);
        assertEquals(resource, result);
    }

    @Test
    void successCreate() {
        long postId = new Random().nextLong(1, 10000);
        MultipartFile file = mock(MultipartFile.class);
        Post post = Post.builder().id(postId).build();
        when(postService.getById(postId)).thenReturn(post);
        when(amazonS3Service.uploadFile(file, userId)).thenReturn(key);
        when(resourceRepository.save(any(Resource.class))).thenAnswer(i -> i.getArguments()[0]);
        List<ResourceDto> result = resourceServiceImpl.create(postId, userId, List.of(file));
        verify(postService, times(1)).getById(postId);
        verify(amazonS3Service, times(1)).uploadFile(file, userId);
        verify(resourceRepository, times(1)).save(any(Resource.class));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void successDownloadResource() {
        S3ObjectInputStream inputStreamMock = mock(S3ObjectInputStream.class);
        when(amazonS3Service.downloadFile(key, userId)).thenReturn(inputStreamMock);
        InputStream result = resourceServiceImpl.download(key, userId);
        assertEquals(inputStreamMock, result);
    }

    @Test
    void successDeleteFile() {
        Post post = Post.builder().id(1L).authorId(1L).build();
        Resource resource = Resource.builder().id(1L).post(post).build();
        when(resourceRepository.findByKey(key)).thenReturn(resource);
        resourceServiceImpl.delete(key, userId);
        verify(resourceValidator, times(1)).validateExistenceByKey(key);
        verify(resourceRepository, times(1)).deleteByKey(key);
        verify(amazonS3Service, times(1)).deleteFile(key, userId);
    }
}