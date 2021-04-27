package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.service.ViewAuthorizer
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

@RestController
class EventController(
    //TODO use proper authorizer for SSE endpoint
    private val viewAuthorizer: ViewAuthorizer
) {

    @GetMapping(value = ["game/v1/events"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getResourceUsage(@RequestParam(required = false) token: String?): Flux<String> {
        return try {
            viewAuthorizer.authorize(token)
            val random = Random()
            Flux.interval(Duration.ofMillis(250))
                .map { random.nextInt().toString() }
        } catch (ex: HttpClientErrorException) {
            Flux.empty()
        }
    }
}