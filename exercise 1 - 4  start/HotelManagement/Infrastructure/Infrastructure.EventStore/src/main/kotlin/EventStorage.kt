import com.github.msemys.esjc.EventStore
import com.github.msemys.esjc.Position
import com.github.msemys.esjc.ResolvedEvent
import workshop.hotels.infrastructure.eventstore.abstractions.*
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EventStorage(private val eventStore: EventStore) : IEventStorage {

    private val readBatchSize:Int = 512

    override fun read(streamId: String, start: Long?, count: Int?): Array<IEventData> {
        return read(streamId, start, count, null)
    }

    override fun read(streamId: String, start: Long?, count: Int?, userCredentials: IUserCredentials?): Array<IEventData>
    {
        val totalReadSize: Int = count ?: readBatchSize
        val startPosition = start ?: 0

        val result = if(userCredentials != null)
            eventStore.readStreamEventsForward(streamId, startPosition, totalReadSize, true,
                    com.github.msemys.esjc.UserCredentials(userCredentials.username, userCredentials.password)).get()
        else
            eventStore.readStreamEventsForward(streamId, startPosition, totalReadSize, true).get()

        return result.events.map{ re -> fromDb(re)}.toTypedArray()
    }

    private fun fromDb(resolvedEvent: ResolvedEvent): IEventData
    {
        val metadata = UserMetadata(deserialize(resolvedEvent.event.metadata) ?: mutableMapOf<String, Any>())
        val metaSerializedTypeKeyValuePair = metadata.tryGet<String>("SerializedType")
        if (!metaSerializedTypeKeyValuePair.first)
        {
            throw IllegalStateException("SerializedType is missing for event ${resolvedEvent.event.eventNumber}@${resolvedEvent.event.eventStreamId}")
        }

        var clazz = Class.forName(metaSerializedTypeKeyValuePair.second)

        val eventData = EventData()
        eventData.eventId = resolvedEvent.event.eventId
        eventData.eventType = resolvedEvent.event.eventType
        eventData.streamId = resolvedEvent.event.eventStreamId
        eventData.eventNumber = resolvedEvent.event.eventNumber
        val obj = deserialize<Any>(resolvedEvent.event.data)
        val event = mapToObject(obj, clazz)
        eventData.event = event
        eventData.metadata = metadata
        eventData.position = if(resolvedEvent.originalPosition != null) EventStorePosition(resolvedEvent.originalPosition) else EventStorePosition(
            Position.END
        )
        return eventData
    }

    override fun save(streamId: String, events: Array<IEvent>, expectedVersion: Long, metadata: UserMetadata): Long
    {
        return save(streamId, events, expectedVersion, metadata, null)
    }

    override fun save(
        streamId: String, events: Array<IEvent>, expectedVersion: Long, metadata: UserMetadata,
        userCredentials: IUserCredentials?
    ): Long
    {
        val eventDatas = events.map{ e -> toDb(e, metadata)}.toMutableList()
        val result = if(userCredentials != null)
            eventStore.appendToStream(streamId, expectedVersion, eventDatas,
                    com.github.msemys.esjc.UserCredentials(userCredentials.username, userCredentials.password)).get()
        else
            eventStore.appendToStream(streamId, expectedVersion, eventDatas).get()

       return result.nextExpectedVersion
    }

    private fun toDb(event: IEvent, metadata: UserMetadata): com.github.msemys.esjc.EventData
    {
        val clazz = event::class
        val serializedEvent = serialize(event)
        val eventMetadata = UserMetadata(metadata.value)
        eventMetadata.add("SerializedType", "${clazz.qualifiedName}")
        val serializedMetadata = serialize(eventMetadata.value)

        return com.github.msemys.esjc.EventData.newBuilder()
                .eventId(UUID.randomUUID())
                .type(clazz.simpleName)
                .data(serializedEvent)
                .metadata(serializedMetadata)
                .build()
    }

    private inline fun <reified T: Any> serialize(objectToSerialize: T): ByteArray
    {
        val charset = Charsets.UTF_8
        val gson = Gson()
        val json = gson.toJson(objectToSerialize)
        return json.toByteArray(charset)
    }

    private fun deserialize(data: ByteArray): MutableMap<String, Any>?
    {
        return try {
            val charset = Charsets.UTF_8
            val json = data.toString(charset)
            val gson = Gson()
            val map = mutableMapOf<String, Any>()
            return gson.fromJson(json, map::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> mapToObject(map: Any, type: Class<T>): T? {
        val gson = Gson()
        val json = gson.toJson(map)
        return gson.fromJson(json, type)
    }

    private inline fun <reified T> deserialize(data: ByteArray): T
    {
        val charset = Charsets.UTF_8
        val json = data.toString(charset)
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }
}
