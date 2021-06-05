package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.model.GameEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Service
class GameEventManager {
    private val gamePublisherMap: ConcurrentMap<String, ConnectableFlux<GameEvent>> = ConcurrentHashMap()
    private val eventConsumerMap: ConcurrentMap<String, (GameEvent) -> Unit> = ConcurrentHashMap()

    fun publishEvent(event: GameEvent) {
        eventConsumerMap[event.gameCode]?.invoke(event)
    }

    fun createNewPublisherForGame(gameCode: String) {
        gamePublisherMap[gameCode] = Flux.create { sink: FluxSink<GameEvent> ->
            eventConsumerMap[gameCode] = { sink.next(it) }
        }.publish()
    }

    fun getPublisherForGame(gameCode: String) = gamePublisherMap[gameCode]
}