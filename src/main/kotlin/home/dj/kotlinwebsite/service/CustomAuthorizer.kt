package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.config.security.AuthenticationManager
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Mono
import java.lang.Exception

@Component
class CustomAuthorizer(
    private val authenticationManager: AuthenticationManager
) {
    fun authorize(token: String?) {
        try {
            val auth = UsernamePasswordAuthenticationToken(token!!, token)
            authenticationManager.authenticate(auth)
                .switchIfEmpty(Mono.error(HttpClientErrorException(HttpStatus.UNAUTHORIZED)))
                .block()
        } catch (ex: Exception) {
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
    }
}