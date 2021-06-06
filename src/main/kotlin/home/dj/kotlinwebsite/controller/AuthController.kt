package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.AuthRequestDTO
import home.dj.kotlinwebsite.model.AuthResponseDTO
import home.dj.kotlinwebsite.service.UserService
import home.dj.kotlinwebsite.util.JwtTokenUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class AuthController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping(value = ["/v1/authenticate"], consumes = ["application/json"])
    fun createAuthenticationToken(@RequestBody authRequestDTO: AuthRequestDTO): Mono<ResponseEntity<AuthResponseDTO>> {
        return userService.getUser(authRequestDTO.username).map { user ->
            if (passwordEncoder.encode(authRequestDTO.password).equals(user.password)) {
                return@map ResponseEntity.ok(AuthResponseDTO(jwtTokenUtil.generateToken(user)))
            } else {
                return@map ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            }
        }.defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
    }
}