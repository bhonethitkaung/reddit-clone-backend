package com.example.redditclone.service;

import com.example.redditclone.entity.Post;
import com.example.redditclone.entity.Subreddit;
import com.example.redditclone.entity.User;
import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.model.PostRequest;
import com.example.redditclone.model.PostResponse;
import com.example.redditclone.repository.CommentRepository;
import com.example.redditclone.repository.PostRepository;
import com.example.redditclone.repository.SubredditRepository;
import com.example.redditclone.repository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SubredditRepository subredditRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    public void createPost(PostRequest postRequest) {
        postRepository.save(postRequestToEntity(postRequest));
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new SpringRedditCloneException("Post not found with id: " + id));
        return entityToPostResponse(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::entityToPostResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getAllPostsBySubredditName(String subredditName) {
        Subreddit subreddit = subredditRepository.findBySubredditName(subredditName).orElseThrow(() -> new SpringRedditCloneException("Subreddit not found with name: " + subredditName));
        return postRepository.findAllBySubreddit(subreddit).stream()
                .map(this::entityToPostResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getAllPostsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditCloneException("User not found with username: " + username));
        return postRepository.findAllByUser(user).stream()
                .map(this::entityToPostResponse)
                .collect(Collectors.toList());
    }

    private Post postRequestToEntity(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findBySubredditName(postRequest.getSubredditName()).orElseThrow(() -> new SpringRedditCloneException("Subreddit not found with name: " + postRequest.getSubredditName()));
        return Post.builder()
                .postName(postRequest.getPostName())
                .url(postRequest.getUrl())
                .description(postRequest.getDescription())
                .createdDate(Instant.now())
                .user(authService.getCurrentUser())
                .subreddit(subreddit)
                .voteCount(0)
                .build();
    }

    private PostResponse entityToPostResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .postName(post.getPostName())
                .description(post.getDescription())
                .subredditName(post.getSubreddit().getSubredditName())
                .duration(getDuration(post.getCreatedDate()))
                .username(post.getUser().getUsername())
                .url(post.getUrl())
                .voteCount(post.getVoteCount())
                .commentCount(commentRepository.findAllByPost(post).size())
                .build();
    }

    private String getDuration(Instant createdDate) {
        return TimeAgo.using(createdDate.toEpochMilli());
    }


}
