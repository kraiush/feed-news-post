package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FromDateFilterTest {

    private Album acceptableAlbum;
    private Album nonAcceptableAlbum;
    private AlbumFilterDto albumFilterDto;
    private FromDateFilter albumFromDateFilter;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .fromDate(LocalDateTime.now().minusDays(2)).build();
        albumFromDateFilter = new FromDateFilter();
        acceptableAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        nonAcceptableAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(10))
                .build();
    }

    @Test
    @DisplayName("testing isAcceptable method with non acceptable value")
    void testIsApplicableNonAcceptableValue() {
        AlbumFilterDto nonAcceptableAlbumFilterDto = new AlbumFilterDto();
        assertFalse(albumFromDateFilter.isAcceptable(nonAcceptableAlbumFilterDto));
    }

    @Test
    @DisplayName("testing isAcceptable method with acceptable value")
    void testIsApplicableWithAcceptableValue() {
        assertTrue(albumFromDateFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        List<Album> acceptableAlbumList = albumFromDateFilter
                .applyFilter(Stream.of(acceptableAlbum, nonAcceptableAlbum), albumFilterDto)
                .toList();
        assertEquals(1, acceptableAlbumList.size());
        assertEquals(acceptableAlbum, acceptableAlbumList.get(0));
    }
}