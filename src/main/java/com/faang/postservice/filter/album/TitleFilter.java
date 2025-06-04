package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TitleFilter implements AlbumFilter {

    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null && !albumFilterDto.getTitlePattern().isEmpty();
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isAcceptable(albumFilterDto)) {
            return albumStream.filter(album -> album.getTitle().startsWith(albumFilterDto.getTitlePattern()));
        }
        return albumStream;
    }
}