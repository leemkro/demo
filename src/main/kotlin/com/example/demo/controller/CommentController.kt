package com.example.demo.controller

import com.example.demo.dto.CommentResponse
import com.example.demo.dto.CreateCommentRequest
import com.example.demo.security.UserPrincipal
import com.example.demo.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CommentController(
    private val commentService: CommentService
) {

    @GetMapping("/posts/{postId}/comments")
    fun getComments(@PathVariable postId: String): ResponseEntity<List<CommentResponse>> {
        val comments = commentService.getCommentsByPostId(postId)
        return ResponseEntity.ok(comments)
    }

    @PostMapping("/posts/{postId}/comments")
    fun createComment(
        @PathVariable postId: String,
        @Valid @RequestBody request: CreateCommentRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<CommentResponse> {
        val comment = commentService.createComment(postId, request, userPrincipal.id, userPrincipal.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(comment)
    }

    @DeleteMapping("/comments/{id}")
    fun deleteComment(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        return try {
            commentService.deleteComment(id, userPrincipal.id)
            ResponseEntity.ok(mapOf("message" to "Comment deleted successfully"))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalAccessException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }
}