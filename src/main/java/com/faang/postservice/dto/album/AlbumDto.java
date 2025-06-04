package com.faang.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AlbumDto {

    private Long id;
    @NotBlank(message = "Invalid Title: Empty title")
    @NotBlank(message = "Title is required")
    @Size(max = 256, message = "Invalid Title: Exceeds 256 characters")
    private String title;

    @NotBlank(message = "Invalid Description: Empty description")
    @NotNull(message = "Description must be provided")
    @Size(max = 4096, message = "Invalid Title: Exceeds 4096 characters")
    private String description;

    private List<Long> postIds;
}
