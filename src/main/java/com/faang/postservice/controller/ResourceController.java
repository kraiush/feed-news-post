package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.resource.ResourceDto;
import com.faang.postservice.service.resource.ResourceService;
import com.faang.postservice.validation.s3.files.FileRequiredInList;
import com.faang.postservice.validation.s3.files.MaxFileSizeInList;
import com.faang.postservice.validation.s3.files.ValidFileTypeInList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Tag(name = "Resource service", description = "management APIs")
@RestController
@RequestMapping("resources")
@RequiredArgsConstructor
@Validated
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @Operation(summary = "Save a resource")
    @PostMapping("/{postId}")
    public List<ResourceDto> createResources(
            @FileRequiredInList
            @MaxFileSizeInList
            @RequestParam(value = "files") @ValidFileTypeInList List<MultipartFile> files,
            @PathVariable("postId") long postId) {
        return resourceService.create(postId, userContext.getUserId(), files);
    }

    @Operation(summary = "Download the resource")
    @GetMapping("/{key}")
    public InputStream downloadFile(@PathVariable String key) {
        return resourceService.download(key, userContext.getUserId());
    }

    @Operation(summary = "Delete the resource")
    @DeleteMapping("/{key}")
    public void deleteFile(@PathVariable String key) {
        resourceService.delete(key, userContext.getUserId());
    }
}
