package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionFilterTest {

    private Album acceptableAlbum;
    private Album nonAcceptableAlbum;
    private DescriptionFilter albumDescriptionFilter;

    @BeforeEach
    public void setUp() {
        albumDescriptionFilter = new DescriptionFilter();
        acceptableAlbum = Album.builder()
                .description("description")
                .build();
        nonAcceptableAlbum = Album.builder()
                .description("not")
                .build();
    }

    @Test
    @DisplayName("testing isAcceptable method with non acceptable value")
    void testIsApplicableNonAcceptableValue() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        assertFalse(albumDescriptionFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing isAcceptable method with acceptable value")
    void testIsApplicableWithAcceptableValue() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .descriptionPattern("description").build();
        assertTrue(albumDescriptionFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .descriptionPattern("description").build();
        List<Album> acceptableAlbumList = albumDescriptionFilter
                .applyFilter(Stream.of(acceptableAlbum, nonAcceptableAlbum), albumFilterDto)
                .toList();
        assertEquals(1, acceptableAlbumList.size());
        assertEquals(acceptableAlbum, acceptableAlbumList.get(0));
    }
}