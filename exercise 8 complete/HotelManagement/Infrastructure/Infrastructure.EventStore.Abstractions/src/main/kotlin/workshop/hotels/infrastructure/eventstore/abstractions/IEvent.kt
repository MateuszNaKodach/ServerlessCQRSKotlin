package workshop.hotels.infrastructure.eventstore.abstractions

import java.util.*

interface IEvent {
    val id: UUID
}