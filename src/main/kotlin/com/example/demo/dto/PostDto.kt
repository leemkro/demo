package com.example.demo.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreatePostRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Content is required")
    val content: String
)

data class UpdatePostRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Content is required")
    val content: String
)

data class PostResponse(
    val id: String,  // JSON 응답용으로 String 유지
    val title: String,
    val content: String,
    val authorId: String,
    val author: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class PostListResponse(
    val posts: List<PostResponse>,
    val total: Long,
    val totalPages: Int
)
