package com.example.redditclone.repository;

import com.example.redditclone.entity.Post;
import com.example.redditclone.entity.User;
import com.example.redditclone.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User user);
}
