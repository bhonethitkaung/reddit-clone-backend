package com.example.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long postId;
    private String postName;
    private String url;
    private String description;
    private String username;
    private String subredditName;
    private String duration;
    private Integer voteCount;
    private Integer commentCount;
}
