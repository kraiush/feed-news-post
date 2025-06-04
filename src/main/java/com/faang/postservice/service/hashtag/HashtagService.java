package com.faang.postservice.service.hashtag;

import com.faang.postservice.model.Hashtag;
import com.faang.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public Hashtag save(Hashtag entity) {
        return hashtagRepository.save(entity);
    }

    public Optional<Hashtag> getHashtagByName(String name) {
        return hashtagRepository.findHashtagByName(name);
    }

    public int checkDoubling(Long postId, Long hashtagId) {
        return hashtagRepository.checkDoubleEntry(postId, hashtagId);
    }

    public void insertEntry(Long postId, Long hashtagId) {
        hashtagRepository.insertEntry(postId, hashtagId);
    }

    public void deleteByPostId(Long postId) {
        hashtagRepository.deleteHashTagsFromPost(postId);
    }

    public Set<Hashtag> getHashtagsByPostId(Long postId) {
        return hashtagRepository.findHashtagsByPostId(postId);
    }

    public Set<Long> getPostIdsByHashtagName(String name) {
        return hashtagRepository.findPostIdsByHashtagName(name);
    }

    public Set<Object> getPostByHashtagName(String name) {
        return hashtagRepository.findPostsByHashtagName(name);
    }
}
