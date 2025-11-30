package com.example.demo.controller

import com.example.demo.dto.*
import com.example.demo.security.UserPrincipal
import com.example.demo.service.PostService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {

    @GetMapping
    fun getAllPosts(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<PostListResponse> {
        val response = postService.getAllPosts(page, limit, search)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: String): ResponseEntity<PostResponse> {
        return try {
            val post = postService.getPostById(id)
            ResponseEntity.ok(post)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createPost(
        @Valid @RequestBody request: CreatePostRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<PostResponse> {
        val post = postService.createPost(request, userPrincipal.id, userPrincipal.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(post)
    }

    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdatePostRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<PostResponse> {
        return try {
            val post = postService.updatePost(id, request, userPrincipal.id)
            ResponseEntity.ok(post)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalAccessException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletePost(
        @PathVariable id: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        return try {
            postService.deletePost(id, userPrincipal.id)
            ResponseEntity.ok(mapOf("message" to "Post deleted successfully"))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalAccessException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }
}