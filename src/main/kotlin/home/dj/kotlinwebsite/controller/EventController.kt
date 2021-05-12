package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.GameEventDTO
import home.dj.kotlinwebsite.service.CustomAuthorizer
import home.dj.kotlinwebsite.service.GameEventListener
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

@RestController
class EventController(
    private val sseAuthorizer: CustomAuthorizer,
    private val gameEventListener: GameEventListener,
    private val connectableFlux: ConnectableFlux<GameEventDTO> = gameEventListener.createEventHotPublisher()
) {

    @GetMapping(value = ["game/v1/events"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun gameEventStream(@RequestParam(required = false) token: String?): Flux<GameEventDTO> {
        return try {
            sseAuthorizer.authorize(token)
            connectableFlux.autoConnect()
        } catch (ex: HttpClientErrorException) {
            Flux.empty()
        }
    }
}