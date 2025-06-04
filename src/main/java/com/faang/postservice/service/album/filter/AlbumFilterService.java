package com.faang.postservice.service.album.filter;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilterService {
    Stream<Album> applyFilters(Stream<Album> albums, AlbumFilterDto filterDto);
}