package com.example.redditclone.controller;

import com.example.redditclone.model.CommentDTO;
import com.example.redditclone.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity createComment(@RequestBody CommentDTO commentDTO) {
        commentService.createComment(commentDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(commentService.getAllCommentsByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByPostId(@PathVariable("postId") Long postId) {
        return new ResponseEntity<>(commentService.getAllCommentsByPostId(postId), HttpStatus.OK);
    }
}
