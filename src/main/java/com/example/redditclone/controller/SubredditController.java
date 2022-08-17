package com.example.redditclone.controller;

import com.example.redditclone.model.SubredditDTO;
import com.example.redditclone.service.SubredditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddits")
public class SubredditController {

    @Autowired
    private SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDTO> createSubreddit(@RequestBody SubredditDTO subredditDTO) {
        SubredditDTO subredditDTOResponse = subredditService.createSubreddit(subredditDTO);
        return new ResponseEntity<SubredditDTO>(subredditDTOResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SubredditDTO>> getAllSubreddits() {
        List<SubredditDTO> subredditDTOList = subredditService.getAllSubreddits();
        return new ResponseEntity<>(subredditDTOList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDTO> getSubreddit(@PathVariable("id") Long id) {
        SubredditDTO subredditDTO = subredditService.getSubreddit(id);
        return new ResponseEntity<>(subredditDTO, HttpStatus.OK);
    }


}
