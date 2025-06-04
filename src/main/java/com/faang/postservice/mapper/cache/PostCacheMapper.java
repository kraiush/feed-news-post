package com.faang.postservice.mapper.cache;

import com.faang.postservice.model.Hashtag;
import com.faang.postservice.model.Like;
import com.faang.postservice.model.Post;
import com.faang.postservice.dto.cache.PostCache;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = CommentCacheMapper.class
)
public interface PostCacheMapper {

    @Mapping(source = "likes", target = "likeCount", qualifiedByName = "getCountFromLike")
    @Mapping(source = "hashtags", target = "hashtagNames", qualifiedByName = "getHashtagsFromPost")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapCommentsToPostCache")
    PostCache toPostCache(Post post);

    @Named("getHashtagsFromPost")
    default Set<String> getHashtagsFromPost(Set<Hashtag> hashtags) {
        return hashtags == null ? new HashSet<>() : hashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Named("getCountFromLike")
    default int getCountFromLike(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }

    List<PostCache> toListPostCache(List<Post> posts);
}
