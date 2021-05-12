package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.model.GameEventDTO
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

@Service
class GameEventListener {
    lateinit var eventConsumer: (GameEventDTO) -> Unit

    fun createEventHotPublisher(): ConnectableFlux<GameEventDTO> {
        return Flux.create { sink: FluxSink<GameEventDTO> ->
            this.eventConsumer = { sink.next(it) }
        }.publish()
    }

    fun onMessage(event: GameEventDTO) {
        eventConsumer.invoke(event)
    }
}