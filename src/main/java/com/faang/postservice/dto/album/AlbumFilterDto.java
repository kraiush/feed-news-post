package com.faang.postservice.dto.album;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumFilterDto {

    @Size(min = 1, max = 256)
    private String titlePattern;
    @Size(min = 1, max = 4096)
    private String descriptionPattern;
    private LocalDateTime fromDate;
    private LocalDateTime beforeDate;
    private List<Long> authorIdList;
}
