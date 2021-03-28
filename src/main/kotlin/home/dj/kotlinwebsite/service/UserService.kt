package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.config.GeneralProperties
import home.dj.kotlinwebsite.model.User
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Primary
@Service
class UserService(
    private val props: GeneralProperties
) {
    fun getUser(username: String): Mono<User> {
        return if (username == props.username) {
            Mono.just(User(props.username, props.password))
        } else {
            Mono.empty()
        }
    }
}