package com.example.redditclone.service;

import com.example.redditclone.entity.Subreddit;
import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.model.SubredditDTO;
import com.example.redditclone.repository.SubredditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SubredditService {

    @Autowired
    private SubredditRepository subredditRepository;

    @Autowired
    private AuthService authService;

    public SubredditDTO createSubreddit(SubredditDTO subredditDTO) {
        Subreddit subreddit = subredditRepository.save(mapDTOToEntity(subredditDTO));
        subredditDTO.setSubredditId(subreddit.getSubredditId());
        return subredditDTO;
    }

    public List<SubredditDTO> getAllSubreddits() {
        return subredditRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    public SubredditDTO getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditCloneException("Subreddit not found with ID: " + id));
        return mapEntityToDTO(subreddit);
    }

    private Subreddit mapDTOToEntity(SubredditDTO subredditDTO) {
        return Subreddit.builder()
//                .subredditId(subredditDTO.getSubredditId())
                .subredditName("/r/" + subredditDTO.getSubredditName())
                .description(subredditDTO.getDescription())
                .createdDate(Instant.now())
                .user(authService.getCurrentUser())
                .build();
    }

    private SubredditDTO mapEntityToDTO(Subreddit subreddit) {
        return SubredditDTO.builder()
                .subredditId(subreddit.getSubredditId())
                .subredditName(subreddit.getSubredditName())
                .description(subreddit.getDescription())
                .postCount(subreddit.getPosts().size())
                .build();
    }
}
