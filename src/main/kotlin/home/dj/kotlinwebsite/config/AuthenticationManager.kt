package home.dj.kotlinwebsite.config

import home.dj.kotlinwebsite.util.JwtTokenUtil
import io.jsonwebtoken.Claims
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class AuthenticationManager(
    private val jwtTokenUtil: JwtTokenUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val authToken = authentication.credentials.toString()

        try {
            if (jwtTokenUtil.isTokenExpired(authToken)) {
                return Mono.empty()
            }

            val claims: Claims = jwtTokenUtil.getAllClaimsFromToken(authToken)

            return Mono.just(
                UsernamePasswordAuthenticationToken(
                    claims.subject,
                    null,
                    listOf(GrantedAuthority { "ROLE_USER" })
                )
            )
        } catch (e: Exception) {
            return Mono.empty()
        }
    }
}