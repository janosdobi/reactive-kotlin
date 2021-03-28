package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.AuthRequest
import home.dj.kotlinwebsite.model.AuthResponse
import home.dj.kotlinwebsite.service.UserService
import home.dj.kotlinwebsite.util.JwtTokenUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping(value = ["/authenticate"], consumes = ["application/json"])
    fun createAuthenticationToken(@RequestBody authRequest: AuthRequest): Mono<ResponseEntity<AuthResponse>> {
        return userService.getUser(authRequest.username).map { user ->
            if (passwordEncoder.encode(authRequest.password).equals(user.password)) {
                return@map ResponseEntity.ok(AuthResponse(jwtTokenUtil.generateToken(user)))
            } else {
                return@map ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            }
        }.defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
    }
}