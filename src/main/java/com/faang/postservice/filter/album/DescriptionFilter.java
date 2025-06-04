package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;

import java.util.stream.Stream;

public class DescriptionFilter implements AlbumFilter {
    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isAcceptable(albumFilterDto)) {
            return albumStream
                    .filter(album -> album.getDescription()
                            .contains(albumFilterDto.getDescriptionPattern()));
        }
        return albumStream;
    }
}