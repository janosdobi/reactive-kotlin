package home.dj.kotlinwebsite.config.security

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

            return Mono.just(
                UsernamePasswordAuthenticationToken(
                    jwtTokenUtil.getUidFromToken(authToken),
                    null,
                    listOf(GrantedAuthority { jwtTokenUtil.getRoleFromToken(authToken) })
                )
            )
        } catch (e: Exception) {
            return Mono.empty()
        }
    }
}