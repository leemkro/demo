package com.example.demo.service

import com.example.demo.dto.AuthResponse
import com.example.demo.dto.LoginRequest
import com.example.demo.dto.RegisterRequest
import com.example.demo.dto.UserResponse
import com.example.demo.entity.User
import com.example.demo.repository.UserRepository
import com.example.demo.security.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val encodedPassword: String = passwordEncoder.encode(request.password)
            ?: throw IllegalStateException("Password encoding failed")

        val user = User(
            email = request.email,
            username = request.username,
            password = encodedPassword
        )

        val savedUser = userRepository.save(user)
        val userId: UUID = savedUser.id ?: throw IllegalStateException("User ID not generated after save")

        val token = jwtUtil.generateToken(userId, savedUser.email, savedUser.username)

        return AuthResponse(
            user = UserResponse(
                id = userId.toString(),
                email = savedUser.email,
                username = savedUser.username
            ),
            token = token
        )
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val userId: UUID = user.id ?: throw IllegalStateException("User ID is null in database")
        val token = jwtUtil.generateToken(userId, user.email, user.username)

        return AuthResponse(
            user = UserResponse(
                id = userId.toString(),
                email = user.email,
                username = user.username
            ),
            token = token
        )
    }

    @Transactional(readOnly = true)
    fun getCurrentUser(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val foundUserId: UUID = user.id ?: throw IllegalStateException("User ID is null in database")

        return UserResponse(
            id = foundUserId.toString(),
            email = user.email,
            username = user.username
        )
    }
}