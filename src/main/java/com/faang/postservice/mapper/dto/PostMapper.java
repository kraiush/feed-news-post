package com.faang.postservice.mapper.dto;

import com.faang.postservice.dto.post.PostDto;
import com.faang.postservice.model.Hashtag;
import com.faang.postservice.model.Like;
import com.faang.postservice.model.Post;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostMapper {
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLike")
    @Mapping(source = "hashtags", target = "hashtagNames", qualifiedByName = "getHashtagsFromPost")
    PostDto toPostDto(Post post);

    @Named("getCountFromLike")
    default int getCountFromLike(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }

    @Named("getHashtagsFromPost")
    default Set<String> getHashtagsFromPost(Set<Hashtag> hashtags) {
        return hashtags == null ? new HashSet<>() : hashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toCollection(HashSet::new));
    }

    List<PostDto> toListPostDto(List<Post> posts);
}
