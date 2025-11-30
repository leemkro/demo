package com.example.demo.controller

import com.example.demo.dto.*
import com.example.demo.security.UserPrincipal
import com.example.demo.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.register(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.login(request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<Map<String, UserResponse>> {
        val user = authService.getCurrentUser(userPrincipal.id)
        return ResponseEntity.ok(mapOf("user" to user))
    }
}