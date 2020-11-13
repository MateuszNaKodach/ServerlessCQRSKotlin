package workshop.hotels.infrastructure.eventstore.abstractions

import java.util.*

interface IEventData {
    val eventId: UUID
    var eventType: String
    var streamId: String
    var eventNumber: Long
    var event: Any?
    var metadata: IMetadataProvider?
    var position: IPosition?
}