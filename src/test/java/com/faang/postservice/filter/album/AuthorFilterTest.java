package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.*;

class AuthorFilterTest {

    private Album acceptableAlbum;
    private Album nonAcceptableAlbum;
    private AuthorFilter albumAuthorFilter;
    private String description;
    private String title;

    @BeforeEach
    public void setUp() {
        albumAuthorFilter = new AuthorFilter();
        title = randomAlphabetic(257, 1000);
        description = randomAlphabetic(256);
        acceptableAlbum = Album.builder()
                .authorId(1L)
                .build();
        nonAcceptableAlbum = Album.builder()
                .authorId(2L)
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non acceptable value")
    void testIsApplicableNonAcceptableValue() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(title)
                .descriptionPattern(description)
                .build();
        assertFalse(albumAuthorFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with acceptable value")
    void testIsApplicableWithAcceptableValue() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .authorIdList(new ArrayList<>()).build();
        assertTrue(albumAuthorFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .authorIdList(List.of(1L)).build();
        List<Album> acceptableAlbumList = albumAuthorFilter
                .applyFilter(Stream.of(acceptableAlbum, nonAcceptableAlbum), albumFilterDto)
                .toList();
        assertEquals(1, acceptableAlbumList.size());
        assertEquals(acceptableAlbum, acceptableAlbumList.get(0));
    }
}