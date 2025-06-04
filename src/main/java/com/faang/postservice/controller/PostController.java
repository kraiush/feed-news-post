package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.hashtag.HashtagDto;
import com.faang.postservice.dto.post.PostDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.mapper.dto.PostMapper;
import com.faang.postservice.model.Post;
import com.faang.postservice.service.post.PostService;
import com.faang.postservice.validation.post.OnCreate;
import com.faang.postservice.validation.post.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Post service", description = "management APIs")
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostMapper postMapper;
    private final PostService postService;
    private final UserContext userContext;

    private final static String INCORRECT_DATA = "Incorrect input data";
    private final static String INDEPENDENT_ERROR = "The error occurred ndependently of the caller";

    @Operation(summary = "Retrieve a post", description = "Enter the post Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post received",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable(value = "postId") Long postId) {
        long userId = userContext.getUserId();
        Post post = postService.getPostById(postId, userId);
        return postMapper.toPostDto(post);
    }

    @Operation(summary = "Create a post", description = "Enter the post details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})

    })
    @PostMapping
    @Validated(OnCreate.class)
    public PostDto create(@RequestBody @Valid PostDto postDto) {
        long authorId = userContext.getUserId();
        Post entity = MapperUtil.convertClass(postDto, Post.class);
        entity.setAuthorId(authorId);
        var post = postService.create(entity, postDto.getHashtagNames());
        return postMapper.toPostDto(post);
    }

    @Operation(summary = "Update the post", description = "Enter the post details to update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @PutMapping
    public PostDto update(@Valid @RequestBody @Validated({OnUpdate.class, Default.class}) PostDto postDto) {
        long authorId = userContext.getUserId();
        Post entity = MapperUtil.convertClass(postDto, Post.class);
        entity.setAuthorId(authorId);
        var post = postService.update(entity, postDto.getHashtagNames());
        return postMapper.toPostDto(post);
    }

    @Operation(summary = "Publish the post", description = "Enter the post Id to publish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The post sent for publication",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{postId}/publish")
    public PostDto publish(@PathVariable(value = "postId") Long postId) {
        long userId = userContext.getUserId();
        Post post = postService.publish(postId, userId);
        return postMapper.toPostDto(post);
    }

    @Operation(summary = "Delete the post", description = "Enter the post Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted"),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable(value = "postId") Long postId) {
        long userId = userContext.getUserId();
        postService.delete(postId, userId);
    }

    @Operation(summary = "Retrieve unpublished user posts", description = "Enter the user ID to view his unpublished posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of unpublished user posts received", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getAllDraftsPostsByAuthor(@PathVariable Long userId) {
        return postMapper.toListPostDto(postService.getAllDraftsPostsByUserId(userId));
    }

    @Operation(summary = "Retrieve published user posts", description = "Enter the user ID to view his unpublished posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of published user posts received", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/published/user/{userId}")
    public List<PostDto> getAllPublishedPostsByAuthor(@PathVariable Long userId) {
        return postMapper.toListPostDto(postService.getAllPublishedPostsByUserId(userId));
    }

    @GetMapping("/hashtag")
    public List<PostDto> findPostsByHashtag(@RequestBody @Valid HashtagDto hashtag) {
        return postMapper.toListPostDto(postService.getPostsByHashtagName(hashtag.getName()));
    }
}
