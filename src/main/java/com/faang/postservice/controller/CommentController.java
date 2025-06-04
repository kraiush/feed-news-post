package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.comment.CommentDto;
import com.faang.postservice.model.Comment;
import com.faang.postservice.service.comment.CommentServiceImpl;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.validation.comment.CommentCreate;
import com.faang.postservice.validation.comment.CommentUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Comment service", description = "management APIs")
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl service;
    private final UserContext userContext;

    private final static String INCORRECT_DATA = "Incorrect input data";
    private final static String INDEPENDENT_ERROR = "The error occurred ndependently of the caller";

    @Operation(summary = "Retrieve comments by post Id", description = "Enter the post Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments received", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsByPostId(@Parameter(description = "Id of comment to be retrieved", required = true)
                                                @PathVariable(value = "postId") Long postId) {
        var comments = service.getAllByPostId(postId);
        return MapperUtil.convertList(comments, CommentDto.class);
    }

    @Operation(summary = "Retrieve a comment", description = "Enter the comment Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment received",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable(value = "commentId") Long commentId) {
        Comment comment = service.getById(commentId);
        return MapperUtil.convertClass(comment, CommentDto.class);
    }

    @Operation(summary = "Create a comment", description = "Enter the comment details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})

    })
    @PostMapping
    public CommentDto createComment(@Validated(CommentCreate.class) @RequestBody CommentDto commentDto) {
        long userId = userContext.getUserId();
        Comment entity = MapperUtil.convertClass(commentDto, Comment.class);
        entity.setAuthorId(userId);
        var comment = service.create(entity);
        return MapperUtil.convertClass(comment, CommentDto.class);
    }

    @Operation(summary = "Update the comment", description = "Enter the data to update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @PutMapping
    public CommentDto updateComment(@Validated(CommentUpdate.class) @RequestBody CommentDto commentDto) {
        long userId = userContext.getUserId();
        Comment entity = MapperUtil.convertClass(commentDto, Comment.class);
        var comment = service.update(entity, userId);
        return MapperUtil.convertClass(comment, CommentDto.class);
    }

    @Operation(summary = "Delete the comment", description = "Enter the comment Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "400", description = INCORRECT_DATA, content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = INDEPENDENT_ERROR, content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{commentId}")
    public void delete(@Parameter(description = "Id of comment to be retrieved", required = true)
                       @PathVariable(value = "commentId") Long commentId) {
        long userId = userContext.getUserId();
        service.delete(commentId, userId);
    }
}
