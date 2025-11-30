package com.example.demo.repository

import com.example.demo.entity.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PostRepository : JpaRepository<Post, UUID> {
    fun findByTitleContainingOrContentContaining(
        title: String,
        content: String,
        pageable: Pageable
    ): Page<Post>
}
