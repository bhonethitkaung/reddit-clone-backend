package com.example.redditclone.controller;

import com.example.redditclone.model.VoteDTO;
import com.example.redditclone.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ResponseEntity vote(@RequestBody VoteDTO voteDTO) {
        voteService.vote(voteDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
