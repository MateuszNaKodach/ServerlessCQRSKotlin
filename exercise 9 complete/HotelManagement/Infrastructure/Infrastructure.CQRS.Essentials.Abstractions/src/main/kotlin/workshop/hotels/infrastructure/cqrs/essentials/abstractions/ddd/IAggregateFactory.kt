package workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd

import java.util.*

interface IAggregateFactory<TAggregate> where TAggregate: Any, TAggregate :IAggregate
{
    fun get(id: UUID): TAggregate
}