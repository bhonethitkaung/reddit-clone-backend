package com.example.redditclone.model;

import com.example.redditclone.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDTO {

    private VoteType voteType;
    private Long postId;

}
