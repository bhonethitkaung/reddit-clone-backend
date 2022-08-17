package com.example.redditclone.entity;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subreddit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subreddit_id")
    private Long subredditId;

    @NotBlank(message = "Subreddit Name cannot be empty")
    @Column(name = "subreddit_name", nullable = false)
    private String subredditName;

    @Lob
    @Nullable
    private String description;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "subreddit")
    private List<Post> posts;
}
