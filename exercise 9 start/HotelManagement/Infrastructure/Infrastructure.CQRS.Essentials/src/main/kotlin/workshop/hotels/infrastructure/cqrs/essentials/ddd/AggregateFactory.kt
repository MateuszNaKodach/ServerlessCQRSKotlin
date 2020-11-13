package workshop.hotels.infrastructure.cqrs.essentials.ddd

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd.IAggregate
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd.IAggregateFactory
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.es.IEventStoreClient
import workshop.hotels.infrastructure.eventstore.abstractions.IEvent
import java.util.*
import kotlin.reflect.KClass

class AggregateFactory<TAggregate>(private val clazz: KClass<*>, private val eventStoreClient: IEventStoreClient):
    IAggregateFactory<TAggregate> where TAggregate: Any, TAggregate :IAggregate {

    override fun get(id: UUID): TAggregate {

        @Suppress("UNCHECKED_CAST") var aggregate = clazz.constructors.first { it.parameters.isEmpty() }.call() as TAggregate
        val streamId = clazz.simpleName + "-" + id
        val eventDatas = eventStoreClient.read(streamId)

        for (eventData in eventDatas) {
            eventData.event?.let {
                aggregate.hydrate(it as IEvent) //hydrate aggregate
            }
        }

        return aggregate
    }
}