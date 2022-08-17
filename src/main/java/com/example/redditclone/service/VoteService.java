package com.example.redditclone.service;

import com.example.redditclone.entity.Post;
import com.example.redditclone.entity.Vote;
import com.example.redditclone.enums.VoteType;
import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.model.VoteDTO;
import com.example.redditclone.repository.PostRepository;
import com.example.redditclone.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PostRepository postRepository;

    public void vote(VoteDTO voteDTO) {
        Post post = postRepository.findById(voteDTO.getPostId()).orElseThrow(() -> new SpringRedditCloneException("Post not found with id: " + voteDTO.getPostId()));
        Optional<Vote> vote = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if(vote.isPresent() && vote.get().getVoteType().equals(voteDTO.getVoteType())) {
            throw new SpringRedditCloneException("You have already " + voteDTO.getVoteType() + "D for this post");
        }
        if(vote.isPresent()) {
            vote.get().setVoteType(voteDTO.getVoteType());
            voteRepository.save(vote.get());

            if(voteDTO.getVoteType().equals(VoteType.UPVOTE)) {
                post.setVoteCount(post.getVoteCount() + 2);
            } else if(voteDTO.getVoteType().equals(VoteType.DOWNVOTE)) {
                post.setVoteCount(post.getVoteCount() - 2);
            }
        } else {
            voteRepository.save(voteDTOToEntity(voteDTO));

            if(voteDTO.getVoteType().equals(VoteType.UPVOTE)) {
                post.setVoteCount(post.getVoteCount() + 1);
            } else if(voteDTO.getVoteType().equals(VoteType.DOWNVOTE)) {
                post.setVoteCount(post.getVoteCount() - 1);
            }
        }
        postRepository.save(post);
    }

    private Vote voteDTOToEntity(VoteDTO voteDTO) {
        Post post = postRepository.findById(voteDTO.getPostId()).orElseThrow(() -> new SpringRedditCloneException("Post not found with ID: " + voteDTO.getPostId()));
        return Vote.builder()
                .user(authService.getCurrentUser())
                .voteType(voteDTO.getVoteType())
                .post(post)
                .build();
    }

    private VoteDTO entityToVoteDTO(Vote vote) {
        return VoteDTO.builder()
                .voteType(vote.getVoteType())
                .postId(vote.getPost().getPostId())
                .build();
    }
}
