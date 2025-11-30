package com.example.demo.repository

import com.example.demo.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CommentRepository : JpaRepository<Comment, UUID> {
    fun findByPostIdOrderByCreatedAtDesc(postId: UUID): List<Comment>
}
