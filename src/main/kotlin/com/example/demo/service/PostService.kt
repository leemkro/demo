package com.example.demo.service

import com.example.demo.dto.CreatePostRequest
import com.example.demo.dto.PostListResponse
import com.example.demo.dto.PostResponse
import com.example.demo.dto.UpdatePostRequest
import com.example.demo.entity.Post
import com.example.demo.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class PostService(
    private val postRepository: PostRepository
) {

    @Transactional(readOnly = true)
    fun getAllPosts(page: Int, limit: Int, search: String?): PostListResponse {
        val pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"))

        val postsPage = if (search.isNullOrBlank()) {
            postRepository.findAll(pageable)
        } else {
            postRepository.findByTitleContainingOrContentContaining(search, search, pageable)
        }

        val posts = postsPage.content.map { it.toResponse() }

        return PostListResponse(
            posts = posts,
            total = postsPage.totalElements,
            totalPages = postsPage.totalPages
        )
    }

    @Transactional(readOnly = true)
    fun getPostById(id: String): PostResponse {
        val uuid = UUID.fromString(id)
        val post = postRepository.findById(uuid)
            .orElseThrow { NoSuchElementException("Post not found") }
        return post.toResponse()
    }

    @Transactional
    fun createPost(request: CreatePostRequest, authorId: UUID, author: String): PostResponse {
        val post = Post(
            title = request.title,
            content = request.content,
            authorId = authorId,
            author = author
        )

        val savedPost = postRepository.save(post)
        return savedPost.toResponse()
    }

    @Transactional
    fun updatePost(id: String, request: UpdatePostRequest, userId: UUID): PostResponse {
        val uuid = UUID.fromString(id)
        val post = postRepository.findById(uuid)
            .orElseThrow { NoSuchElementException("Post not found") }

        if (post.authorId != userId) {
            throw IllegalAccessException("You are not authorized to update this post")
        }

        val updatedPost = post.copy(
            title = request.title,
            content = request.content,
            updatedAt = LocalDateTime.now()
        )

        val savedPost = postRepository.save(updatedPost)
        return savedPost.toResponse()
    }

    @Transactional
    fun deletePost(id: String, userId: UUID) {
        val uuid = UUID.fromString(id)
        val post = postRepository.findById(uuid)
            .orElseThrow { NoSuchElementException("Post not found") }

        if (post.authorId != userId) {
            throw IllegalAccessException("You are not authorized to delete this post")
        }

        postRepository.delete(post)
    }

    private fun Post.toResponse(): PostResponse {
        return PostResponse(
            id = (this.id ?: throw IllegalStateException("Post ID is null")).toString(),
            title = this.title,
            content = this.content,
            authorId = this.authorId.toString(),
            author = this.author,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
