package com.faang.postservice.service.album;

import com.amazonaws.services.kms.model.NotFoundException;
import com.faang.postservice.dto.album.AlbumDto;
import com.faang.postservice.dto.album.AlbumFilterDto;
import com.faang.postservice.mapper.dto.AlbumMapper;
import com.faang.postservice.model.Album;
import com.faang.postservice.model.Post;
import com.faang.postservice.repository.AlbumRepository;
import com.faang.postservice.repository.PostRepository;
import com.faang.postservice.service.album.filter.AlbumFilterService;
import com.faang.postservice.validation.album.AlbumValidator;
import com.faang.postservice.validation.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    @InjectMocks
    private AlbumServiceImpl albumService;
    @Mock
    private AlbumFilterService albumFilterService;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumValidator albumValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserValidator userValidator;

    private String description;
    private String title;
    private Album album;
    private Post post;
    private AlbumDto albumDto;
    private AlbumFilterDto filter;
    private List<Album> albums;

    @BeforeEach
    public void setUp() {
        long postId = new Random().nextLong(1, 500);
        long authorId = new Random().nextLong(1, 1000);
        title = randomAlphabetic(10);
        description = randomAlphabetic(10);
        album = Album.builder().authorId(authorId).title(title).description(description).posts(new ArrayList<>()).build();
        albumDto = AlbumDto.builder().id(authorId).title(title).description(description).build();
        post = Post.builder().id(postId).build();
        filter = AlbumFilterDto.builder().titlePattern(title).descriptionPattern(description).build();
        albums = List.of(album);
    }

    @Test
    void givenNewAlbum_whenSave_thenSuccess() {
        when(albumMapper.toEntity(albumDto)).thenReturn(album);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        AlbumDto result = albumService.createAlbum(1L, albumDto);
        assertNotNull(result);
        verify(albumMapper).toEntity(albumDto);
        verify(userValidator).existUser(anyLong());
        verify(albumValidator).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator).validateAlbumTitleIsUnique(anyLong(), any());
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void addPostToAlbum_whenSave_thenSuccess() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.addPostToAlbum(1L, 1L, 1L);
        assertNotNull(result);
        verify(albumRepository).findById(anyLong());
        verify(postRepository).findById(anyLong());
        verify(userValidator).existUser(anyLong());
        verify(albumValidator).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator).checkPostExistenceInAlbum(any(Album.class), anyLong());
        verify(albumRepository).save(any(Album.class));
        verify(albumMapper).toDto(any(Album.class));
    }

    @Test
    void addAlbumToFavorites_whenSave_thenSuccess() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.addAlbumToFavorites(1L, 1L);
        assertNotNull(result);
        verify(albumRepository).findById(anyLong());
        verify(userValidator).existUser(anyLong());
        verify(albumValidator).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator).validateAlbumExistence(any(Album.class), anyLong());
        verify(albumMapper).toDto(any(Album.class));
    }

    @Test
    void getUserAlbums() {
        when(albumRepository.findByAuthorId(1L)).thenReturn(albums.stream());
        when(albumFilterService.applyFilters(any(), any(AlbumFilterDto.class)))
                .thenReturn(albums.stream());
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllUserAlbums(1L, filter);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository).findByAuthorId(1L);
        verify(albumFilterService).applyFilters(any(), any(AlbumFilterDto.class));
        verify(albumMapper).toDto(any(Album.class));
    }

    @Test
    void getUserFavoriteAlbums() {
        AlbumFilterDto filter = new AlbumFilterDto();
        when(albumRepository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(albums.stream());
        when(albumFilterService.applyFilters(any(), any(AlbumFilterDto.class)))
                .thenReturn(albums.stream());
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllUserFavoriteAlbums(1L, filter);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
        verify(albumFilterService, times(1)).applyFilters(any(), any(AlbumFilterDto.class));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void getAllAlbums() {
        when(albumRepository.findAll()).thenReturn(albums);
        when(albumFilterService.applyFilters(any(), any(AlbumFilterDto.class)))
                .thenReturn(albums.stream());
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumValidator.validateAccess(any(Album.class), anyLong())).thenReturn(true);
        List<AlbumDto> result = albumService.getAllAlbums(1L, filter);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(albumRepository, times(1)).findAll();
        verify(albumFilterService, times(1)).applyFilters(any(), any(AlbumFilterDto.class));
        verify(albumValidator, times(1)).validateAccess(any(Album.class), eq(1L));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void givenAlbumId_getAlbumById() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.getAlbumById(2L, 1L);
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).existUser(anyLong());
        verify(albumValidator, times(1)).validateAccess(any(Album.class), eq(2L));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void givenAlbumObject_whenUpdate_thenReturnUpdatedAlbum() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        AlbumDto result = albumService.updateAlbum(1L, 1L, albumDto);
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).existUser(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).validateAlbumTitleIsUnique(anyLong(), any());
        verify(albumMapper, times(1)).toDto(any(Album.class));
        verify(albumMapper, times(1)).update(any(AlbumDto.class), any(Album.class));
        verify(albumRepository, times(1)).save(any(Album.class));
    }

    @Test
    void givenAlbumId_whenDelete_thenNothing() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.deleteAlbum(1L, 1L);
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).existUser(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumRepository, times(1)).deleteById(anyLong());
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void givenAlbumId_whenDeleteFromFavorites_thenNothing() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.removeAlbumFromFavorite(1L, 1L);
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).existUser(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).validateAlbumExistence(any(Album.class), anyLong());
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(anyLong(), anyLong());
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void givenPostId_whenDeleteFromAlbum_thenNothing() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.removePostFromAlbum(1L, 1L, 1L);
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
        verify(userValidator, times(1)).existUser(anyLong());
        verify(albumValidator, times(1)).validateUserIsAuthor(any(Album.class), anyLong());
        verify(albumValidator, times(1)).checkPostExistenceInAlbum(any(Album.class), anyLong());
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(albumMapper, times(1)).toDto(any(Album.class));
    }

    @Test
    void givenAlbumId_getAllAlbumsById() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        Album result = albumService.findById(albumRepository, 1L, "Album");
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(anyLong());
    }

    @Test
    void givenAlbumId_whenNotFound_thenNothing() {
        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> albumService.findById(albumRepository, 1L, "Album"));
        verify(albumRepository, times(1)).findById(anyLong());
    }
}