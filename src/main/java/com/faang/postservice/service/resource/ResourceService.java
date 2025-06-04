package com.faang.postservice.service.resource;

import com.faang.postservice.dto.resource.ResourceDto;
import com.faang.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ResourceService {

    Resource findById(Long id);

    List<ResourceDto> create(Long postId, Long userId, List<MultipartFile> files);

    InputStream download(String key, Long userId);

    void delete(String key, Long userId);
}
