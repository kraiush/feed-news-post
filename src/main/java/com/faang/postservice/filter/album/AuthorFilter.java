package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;

import java.util.stream.Stream;

public class AuthorFilter implements AlbumFilter {

    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getAuthorIdList() != null;
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isAcceptable(albumFilterDto)) {
            return albumStream.filter(album -> albumFilterDto.getAuthorIdList().contains(album.getAuthorId()));
        }
        return albumStream;
    }
}