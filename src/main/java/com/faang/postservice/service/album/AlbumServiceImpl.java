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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumValidator albumValidator;
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumFilterService albumFilterService;
    private final UserValidator userValidator;

    @Override
    public AlbumDto createAlbum(long userId, AlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        album.setAuthorId(userId);
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(album, userId);
        albumValidator.validateAlbumTitleIsUnique(userId, album.getTitle());
        albumRepository.save(album);
        log.info("Saved album: {} to userId: {}", album.getId(), userId);
        return albumDto;
    }

    @Override
    public AlbumDto addPostToAlbum(long albumId, long postId, long userId) {
        Album album = findById(albumRepository, albumId, "Album");
        Post post = findById(postRepository, postId, "Post");
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(album, userId);
        albumValidator.checkPostExistenceInAlbum(album, postId);
        album.addPost(post);
        albumRepository.save(album);
        log.info("Added postId: {} to albumId: {} for userId: {}", postId, albumId, userId);
        return albumMapper.toDto(album);
    }

    @Override
    @Transactional
    public AlbumDto addAlbumToFavorites(long albumId, long userId) {
        Album album = findById(albumRepository, albumId, "Album");
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(album, userId);
        albumValidator.validateAlbumExistence(album, userId);
        albumRepository.addAlbumToFavorites(albumId, userId);
        log.info("Added albumId: {} to favorites for userId: {}", albumId, userId);
        return albumMapper.toDto(album);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto> getAllUserAlbums(long userId, AlbumFilterDto filter) {
        return albumFilterService.applyFilters(albumRepository.findByAuthorId(userId), filter)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto> getAllUserFavoriteAlbums(long userId, AlbumFilterDto filter) {
        return albumFilterService.applyFilters(albumRepository.findFavoriteAlbumsByUserId(userId), filter)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto> getAllAlbums(long userId, AlbumFilterDto filter) {
        return albumFilterService.applyFilters(albumRepository.findAll().stream(), filter)
                .filter(album -> albumValidator.validateAccess(album, userId))
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(long userId, long albumId) {
        Album album = findById(albumRepository, albumId, "Album");
        userValidator.existUser(userId);
        albumValidator.validateAccess(album, userId);
        log.info("Has got album by albumId: {}", albumId);
        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto updateAlbum(long albumId, long userId, AlbumDto updatedAlbumDto) {
        Album albumToUpdate = findById(albumRepository, albumId, "Album");
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(albumToUpdate, userId);
        albumValidator.validateAlbumTitleIsUnique(userId, updatedAlbumDto.getTitle());
        albumMapper.update(updatedAlbumDto, albumToUpdate);
        log.info("Updated albumId {} for userId {}", albumId, userId);
        return albumMapper.toDto(albumRepository.save(albumToUpdate));
    }

    @Override
    public AlbumDto deleteAlbum(long albumId, long userId) {
        Album album = findById(albumRepository, albumId, "Album");
        AlbumDto albumToDelete = albumMapper.toDto(album);
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(album, userId);
        albumRepository.deleteById(albumId);
        log.info("Deleted albumId: {} for userId: {}", albumId, userId);
        return albumToDelete;
    }

    @Override
    public AlbumDto removeAlbumFromFavorite(long albumId, long userId) {
        Album album = findById(albumRepository, albumId, "Album");
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(album, userId);
        albumValidator.validateAlbumExistence(album, userId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        log.info("Deleted albumId: {} from favorites for userId: {}", albumId, userId);
        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto removePostFromAlbum(long albumId, long postId, long userId) {
        Album album = findById(albumRepository, albumId, "Album");
        userValidator.existUser(userId);
        albumValidator.validateUserIsAuthor(album, userId);
        albumValidator.checkPostExistenceInAlbum(album, postId);
        album.removePost(postId);
        albumRepository.save(album);
        log.info("Removed postId: {} from albumId: {} for userId: {}", postId, albumId, userId);
        return albumMapper.toDto(album);
    }

    public <T> T findById(CrudRepository<T, Long> repository, long id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s with id %d not found", entityName, id)));
    }
}
