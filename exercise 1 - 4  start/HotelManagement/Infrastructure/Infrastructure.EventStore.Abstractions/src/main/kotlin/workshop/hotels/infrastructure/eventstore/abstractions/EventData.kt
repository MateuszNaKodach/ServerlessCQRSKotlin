package workshop.hotels.infrastructure.eventstore.abstractions

import java.util.*

class EventData: IEventData {
    override var eventId: UUID = UUID.randomUUID()
    override var eventType: String = ""
    override var streamId: String = ""
    override var eventNumber: Long = 0
    override var event: Any? = null
    override var metadata: IMetadataProvider? = null
    override var position: IPosition? = null
}