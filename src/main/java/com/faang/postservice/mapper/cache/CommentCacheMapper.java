package com.faang.postservice.mapper.cache;

import com.faang.postservice.dto.cache.CommentCache;
import com.faang.postservice.model.Comment;
import com.faang.postservice.model.Like;
import com.faang.postservice.model.Post;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface CommentCacheMapper {

    CommentCacheMapper INSTANCE = Mappers.getMapper(CommentCacheMapper.class);

    int commentsBoxSize = 5;

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLike")
    CommentCache toCommentCache(Comment comment);

    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    Comment toComment(CommentCache commentCache);

    @Named("getCountFromLike")
    default long getCountFromLike(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }

    @Named("mapCommentsToPostCache")
    default TreeSet<CommentCache> mapCommentsToPostCache(List<Comment> comments) {
        return comments == null ? new TreeSet<>() : comments.stream()
                .map(this::toCommentCache)
                .limit(commentsBoxSize)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Named("mapPostIdToPost")
    default Post mapPostIdToPost(Long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }
}
