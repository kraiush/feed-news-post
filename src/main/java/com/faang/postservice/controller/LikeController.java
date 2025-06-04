package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.like.LikeDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.service.like.LikeService;
import com.faang.postservice.validation.like.LikeComment;
import com.faang.postservice.validation.like.LikePost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Like service", description = "management APIs")
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserContext userContext;

    private final static String INCORRECT_DATA = "Incorrect input data";
    private final static String INDEPENDENT_ERROR = "The error occurred ndependently of the caller";

    @Operation(summary = "Put a like on the post", description = "Enter the post Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like added to the post",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LikeDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})

    })
    @PostMapping(value = "/post", produces = "application/json")
    public LikeDto addLikeToPost(@Validated(LikePost.class) @RequestBody LikeDto likeDto) {
        var authorId = userContext.getUserId();
        likeDto.setUserId(authorId);
        var like = likeService.addLikeToPost(likeDto);
        return MapperUtil.convertClass(like, LikeDto.class);
    }

    @Operation(summary = "Retrieve post likes", description = "Enter the post Id to get the post likes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Likes received", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LikeDto.class)))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/post/{postId}")
    public List<LikeDto> getLikesByPostId(@PathVariable(value = "postId") Long postId) {
        var listLikes = likeService.getLikesByPostId(postId);
        return MapperUtil.convertList(listLikes, LikeDto.class);
    }

    @Operation(summary = "Remove like from post",
            description = "Enter post and user Ids to remove the like from the post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like removed from the post"),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@Positive @PathVariable("postId") Long postId) {
        var authorId = userContext.getUserId();
        likeService.deleteLikeFromPost(postId, authorId);
    }

    @Operation(summary = "Put a like on the comment", description = "Enter the comment Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like added to the comment",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LikeDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})

    })
    @PostMapping(value = "/comment", produces = "application/json")
    public LikeDto addLikeToComment(@Validated(LikeComment.class) @RequestBody LikeDto likeDto) {
        var authorId = userContext.getUserId();
        likeDto.setUserId(authorId);
        var like = likeService.addLikeToComment(likeDto);
        return MapperUtil.convertClass(like, LikeDto.class);
    }

    @Operation(summary = "Retrieve comment likes", description = "Enter the comment Id to get the comment likes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Likes received", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LikeDto.class)))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/comment/{commentId}")
    public List<LikeDto> getLikesByCommentId(@PathVariable(value = "commentId") Long commentId) {
        var listLikes = likeService.getLikesByCommentId(commentId);
        return MapperUtil.convertList(listLikes, LikeDto.class);
    }

    @Operation(summary = "Remove like from the comment", description = "Enter the comment Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like removed from the comment"),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@Positive @PathVariable("commentId") Long commentId) {
        var authorId = userContext.getUserId();
        likeService.deleteLikeFromComment(commentId, authorId);
    }
}
