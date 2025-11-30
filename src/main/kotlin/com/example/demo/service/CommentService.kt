package com.example.demo.service

import com.example.demo.dto.CommentResponse
import com.example.demo.dto.CreateCommentRequest
import com.example.demo.entity.Comment
import com.example.demo.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CommentService(
    private val commentRepository: CommentRepository
) {

    @Transactional(readOnly = true)
    fun getCommentsByPostId(postId: String): List<CommentResponse> {
        val uuid = UUID.fromString(postId)
        return commentRepository.findByPostIdOrderByCreatedAtDesc(uuid)
            .map { it.toResponse() }
    }

    @Transactional
    fun createComment(postId: String, request: CreateCommentRequest, authorId: UUID, author: String): CommentResponse {
        val postUuid = UUID.fromString(postId)
        val comment = Comment(
            postId = postUuid,
            authorId = authorId,
            author = author,
            content = request.content
        )

        val savedComment = commentRepository.save(comment)
        return savedComment.toResponse()
    }

    @Transactional
    fun deleteComment(id: String, userId: UUID) {
        val uuid = UUID.fromString(id)
        val comment = commentRepository.findById(uuid)
            .orElseThrow { NoSuchElementException("Comment not found") }

        if (comment.authorId != userId) {
            throw IllegalAccessException("You are not authorized to delete this comment")
        }

        commentRepository.delete(comment)
    }

    private fun Comment.toResponse(): CommentResponse {
        return CommentResponse(
            id = (this.id ?: throw IllegalStateException("Comment ID is null")).toString(),
            postId = this.postId.toString(),
            authorId = this.authorId.toString(),
            author = this.author,
            content = this.content,
            createdAt = this.createdAt
        )
    }
}
