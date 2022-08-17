package com.example.redditclone.service;

import com.example.redditclone.entity.Comment;
import com.example.redditclone.entity.Post;
import com.example.redditclone.entity.User;
import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.model.CommentDTO;
import com.example.redditclone.repository.CommentRepository;
import com.example.redditclone.repository.PostRepository;
import com.example.redditclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public void createComment(CommentDTO commentDTO) {
        commentRepository.save(commentDTOToEntity(commentDTO));

        // todo: you can add function to send email when someone commented on your post
    }

    public List<CommentDTO> getAllCommentsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditCloneException("User not found with username: " + username));
        return commentRepository.findAllByUser(user).stream()
                        .map(this::entityToCommentDTO)
                        .collect(Collectors.toList());

    }

    public List<CommentDTO> getAllCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new SpringRedditCloneException("Post not found with ID: " + postId));
        return commentRepository.findAllByPost(post).stream()
                .map(this::entityToCommentDTO)
                .collect(Collectors.toList());

    }

    private Comment commentDTOToEntity(CommentDTO commentDTO) {
        Post post = postRepository.findById(commentDTO.getPostId()).orElseThrow(() -> new SpringRedditCloneException("Post not found with id: " + commentDTO.getPostId()));
        return Comment.builder()
                .text(commentDTO.getText())
                .createdDate(Instant.now())
                .user(authService.getCurrentUser())
                .post(post)
                .build();
    }

    private CommentDTO entityToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .text(comment.getText())
                .postId(comment.getPost().getPostId())
                .createdDate(comment.getCreatedDate())
                .username(comment.getUser().getUsername())
                .build();
    }


}
