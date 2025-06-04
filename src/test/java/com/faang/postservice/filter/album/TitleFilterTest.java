package com.faang.postservice.filter.album;

import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TitleFilterTest {

    private Album acceptableAlbum;
    private Album nonAcceptableAlbum;
    private AlbumFilterDto albumFilterDto;
    private TitleFilter albumTitleFilter;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("title").build();
        albumTitleFilter = new TitleFilter();
        acceptableAlbum = Album.builder()
                .title("title")
                .build();
        nonAcceptableAlbum = Album.builder()
                .title("not")
                .build();
    }

    @Test
    @DisplayName("testing isAcceptable method with non acceptable value")
    void testIsApplicableNonAcceptableValue() {
        AlbumFilterDto nonAcceptableAlbumFilterDto = new AlbumFilterDto();
        assertFalse(albumTitleFilter.isAcceptable(nonAcceptableAlbumFilterDto));
    }

    @Test
    @DisplayName("testing isAcceptable method with acceptable value")
    void testIsApplicableWithAcceptableValue() {
        assertTrue(albumTitleFilter.isAcceptable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        List<Album> acceptableAlbumList = albumTitleFilter
                .applyFilter(Stream.of(acceptableAlbum, nonAcceptableAlbum), albumFilterDto)
                .toList();
        assertEquals(1, acceptableAlbumList.size());
        assertEquals(acceptableAlbum, acceptableAlbumList.get(0));
    }
}