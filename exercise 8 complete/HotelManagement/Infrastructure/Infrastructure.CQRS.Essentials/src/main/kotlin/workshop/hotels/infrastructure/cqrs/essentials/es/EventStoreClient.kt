package workshop.hotels.infrastructure.cqrs.essentials.es

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.IBus
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd.IAggregate
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.es.IEventStoreClient
import workshop.hotels.infrastructure.eventstore.abstractions.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class EventStoreClient(private val bus: IBus, private val eventStore: IEventStorage): IEventStoreClient {

    private val lock = ReentrantLock()

    override fun read(streamId: String, start: Long, userCredentials: IUserCredentials?): Array<IEventData> {
        return eventStore.read(streamId, start, null, userCredentials)
    }

    override fun save(aggregate: IAggregate, id: UUID) {
        val streamId = aggregate::class.simpleName + "-" + id
        val events = aggregate.unCommitedEvents.toTypedArray()
        val expectedVersion = aggregate.version - aggregate.unCommitedEvents.count()
        val metadata = UserMetadata()
        metadata.add("\$correlationId", UUID.randomUUID().toString())
        val timeStamp = System.currentTimeMillis()
        metadata.add("Timestamp", timeStamp)
        val nextExpectedVersion = eventStore.save(streamId, events, expectedVersion, metadata)

        if (expectedVersion == nextExpectedVersion) return; //nothing to publish already was published versions match

        lock.lock()
        try{
            publish(streamId, nextExpectedVersion, expectedVersion)
        }
        finally{
            lock.unlock()
        }
    }

    private fun publish(streamId: String, nextExpectedVersion: Long, expectedVersion: Long)
    {
        var count = (nextExpectedVersion - expectedVersion).toInt()
        var events = eventStore.read(streamId, expectedVersion + 1, count)
        for (event in events)
        {
            bus.publish(event)
        }
    }
}