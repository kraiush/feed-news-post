package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {

    public boolean isAcceptable(AlbumFilterDto albumFilterDto);

    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto albumFilterDto);

}