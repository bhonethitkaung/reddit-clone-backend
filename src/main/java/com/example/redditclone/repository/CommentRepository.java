package com.example.redditclone.repository;

import com.example.redditclone.entity.Comment;
import com.example.redditclone.entity.Post;
import com.example.redditclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUser(User user);
    List<Comment> findAllByPost(Post post);
}
