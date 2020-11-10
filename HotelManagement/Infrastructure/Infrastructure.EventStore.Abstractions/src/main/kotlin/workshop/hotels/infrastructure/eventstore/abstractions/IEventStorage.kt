package workshop.hotels.infrastructure.eventstore.abstractions

interface IEventStorage {
    fun read(streamId: String, start: Long?, count: Int?): Array<IEventData>
    fun read(streamId: String, start: Long?, count: Int?, userCredentials: IUserCredentials?): Array<IEventData>
    fun save(streamId: String, events: Array<IEvent>, expectedVersion: Long, metadata: UserMetadata): Long
    fun save(streamId: String, events: Array<IEvent>, expectedVersion: Long, metadata: UserMetadata, userCredentials: IUserCredentials?): Long
}