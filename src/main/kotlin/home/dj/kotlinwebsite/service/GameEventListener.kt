package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.model.GameEventDTO
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Service
class GameEventListener {
    private val gamePublisherMap: ConcurrentMap<String, ConnectableFlux<GameEventDTO>> = ConcurrentHashMap()
    private val eventConsumerMap: ConcurrentMap<String, (GameEventDTO) -> Unit> = ConcurrentHashMap()

    fun onMessage(event: GameEventDTO) {
        eventConsumerMap[event.gameCode]?.invoke(event)
    }

    fun createNewGame(gameCode: String) {
        gamePublisherMap[gameCode] = createEventHotPublisher(gameCode)
    }

    fun getPublisherForGame(gameCode: String) = gamePublisherMap[gameCode]

    private fun createEventHotPublisher(gameCode: String): ConnectableFlux<GameEventDTO> {
        return Flux.create { sink: FluxSink<GameEventDTO> ->
            eventConsumerMap[gameCode] = { sink.next(it) }
        }.publish()
    }
}