package com.faang.postservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {

    private Long id;
    private String key;
    private String name;
    private String type;
    private long size;
    private LocalDateTime createdAt;
}
