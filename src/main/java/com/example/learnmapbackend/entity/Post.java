package com.example.learnmapbackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    private String locationName;
    private Double latitude;
    private Double longitude;

    private String imageUrls;

    private String tags;

    @Column(name = "is_anonymous")
    @JsonProperty("isAnonymous")
    private boolean isAnonymous = false;

    @Column(name = "like_count")
    private int likeCount = 0;

    @Column(name = "comment_count")
    private int commentCount = 0;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    private Date updatedAt = new Date();

    @Column(name = "type")
    private String type = "normal";

    @Column(name = "review_status")
    private String reviewStatus = "pending";
}