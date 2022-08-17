package com.example.redditclone.controller;

import com.example.redditclone.model.PostRequest;
import com.example.redditclone.model.PostResponse;
import com.example.redditclone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity createPost(@RequestBody PostRequest postRequest) {
        postService.createPost(postRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("id") Long id) {
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    @GetMapping("/by-subreddit/{subredditName}")
    public ResponseEntity<List<PostResponse>> getAllPostsBySubredditName(@PathVariable("subredditName") String subredditName) {
        return new ResponseEntity<>(postService.getAllPostsBySubredditName(subredditName), HttpStatus.OK);
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<PostResponse>> getAllPostsByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(postService.getAllPostsByUsername(username), HttpStatus.OK);
    }

}
