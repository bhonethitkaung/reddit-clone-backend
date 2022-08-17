package com.example.redditclone.entity;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @NotBlank(message = "Post Name should not be empty")
    @Column(name = "post_name", nullable = false)
    private String postName;

    @Nullable
    private String url;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Nullable
    @Lob
    private String description;

    @Column(name = "vote_count", nullable = false)
    private Integer voteCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subreddit_id", referencedColumnName = "subreddit_id")
    private Subreddit subreddit;


}
