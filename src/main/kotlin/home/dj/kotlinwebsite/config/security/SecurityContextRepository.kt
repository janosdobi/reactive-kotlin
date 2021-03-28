package home.dj.kotlinwebsite.config.security

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.security.core.context.SecurityContextImpl

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component

@Component
class SecurityContextRepository(
    private val authenticationManager: AuthenticationManager
) : ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val request = exchange.request
        val authHeader = request.headers.getFirst(AUTHORIZATION)

        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val authToken = authHeader.substring(7)
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
            this.authenticationManager.authenticate(auth).map { authentication -> SecurityContextImpl(authentication) }
        } else {
            Mono.empty()
        }
    }
}