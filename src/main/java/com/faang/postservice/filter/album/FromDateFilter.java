package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Component
public class FromDateFilter implements AlbumFilter {

    @Override
    public boolean isAcceptable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getFromDate() != null;
    }

    @Override
    public Stream<Album> applyFilter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        if (isAcceptable(albumFilterDto)) {
            return albumStream.filter(album -> album.getUpdatedAt().isAfter(albumFilterDto.getFromDate()));
        }
        return albumStream;
    }
}