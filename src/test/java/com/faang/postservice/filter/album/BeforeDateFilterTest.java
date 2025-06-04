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

class BeforeDateFilterTest {

    private Album acceptableAlbum;
    private Album nonAcceptableAlbum;
    private AlbumFilterDto albumFilterDto;
    private BeforeDateFilter beforeDateFilter;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .beforeDate(LocalDateTime.now().minusDays(2))
                .build();
        beforeDateFilter = new BeforeDateFilter();
        acceptableAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(10))
                .build();
        nonAcceptableAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non acceptable value")
    void testIsApplicableNonAcceptableValue() {
        AlbumFilterDto nonAcceptableAlbumFilterDto = new AlbumFilterDto();
        assertFalse(beforeDateFilter.isAcceptable(nonAcceptableAlbumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with acceptable value")
    void testIsApplicableWithAcceptableValue() {
        assertTrue(beforeDateFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        List<Album> acceptableAlbumList = beforeDateFilter
                .applyFilter(Stream.of(acceptableAlbum, nonAcceptableAlbum), albumFilterDto)
                .toList();
        assertEquals(1, acceptableAlbumList.size());
        assertEquals(acceptableAlbum, acceptableAlbumList.get(0));
    }
}