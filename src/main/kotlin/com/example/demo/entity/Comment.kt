package com.example.demo.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "post_id", nullable = false)
    val postId: UUID,

    @Column(name = "author_id", nullable = false)
    val authorId: UUID,

    @Column(nullable = false)
    val author: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
