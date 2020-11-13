package workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd

import workshop.hotels.infrastructure.eventstore.abstractions.*

interface IAggregate {
    var version: Long
    fun hydrate(event: IEvent)
    val unCommitedEvents: MutableList<IEvent>
    fun clearUnCommitedEvents()
}