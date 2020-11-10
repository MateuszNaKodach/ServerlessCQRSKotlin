package workshop.hotels.infrastructure.cqrs.essentials.abstractions.es

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd.IAggregate
import workshop.hotels.infrastructure.eventstore.abstractions.*
import java.util.*

interface IEventStoreClient {
    fun read(streamId: String, start: Long = 0, userCredentials: IUserCredentials? = null): Array<IEventData>
    fun save(aggregate: IAggregate, id: UUID)
}