package com.faang.postservice.service.like;

import com.faang.postservice.dto.like.LikeDto;
import com.faang.postservice.model.Like;

import java.util.List;

public interface LikeService {

    List<Like> getLikesByPostId(Long postId);

    List<Like> getLikesByCommentId(Long commentId);

    Like addLikeToPost(LikeDto like);

    Like addLikeToComment(LikeDto like);

    void deleteLikeFromPost(Long postId, Long userId);

    void deleteLikeFromComment(Long commentId, Long userId);
}
