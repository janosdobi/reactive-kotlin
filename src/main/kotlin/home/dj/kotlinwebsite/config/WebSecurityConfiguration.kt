package home.dj.kotlinwebsite.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class WebSecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
            .authorizeExchange()
            .anyExchange()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .formLogin()

        http
            .csrf { csrf -> csrf.disable() }
        return http.build()
    }
}
