package com.example.demo.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreateCommentRequest(
    @field:NotBlank(message = "Content is required")
    val content: String
)

data class CommentResponse(
    val id: String,  // JSON 응답용으로 String 유지
    val postId: String,
    val authorId: String,
    val author: String,
    val content: String,
    val createdAt: LocalDateTime
)
