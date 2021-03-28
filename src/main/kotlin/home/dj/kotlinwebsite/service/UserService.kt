package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.config.GeneralProperties
import home.dj.kotlinwebsite.model.auth.Role
import home.dj.kotlinwebsite.model.auth.User
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Primary
@Service
class UserService(
    private val props: GeneralProperties
) {
    fun getUser(username: String): Mono<User> {
        return when (username) {
            props.username -> Mono.just(User(props.username, props.password, Role.ROLE_USER.name))
            props.admin -> Mono.just(User(props.admin, props.adminPass, Role.ROLE_ADMIN.name))
            else -> Mono.empty()
        }
    }
}