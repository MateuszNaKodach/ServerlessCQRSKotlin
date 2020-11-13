package workshop.hotels.reservations.domain.models.events

import workshop.hotels.infrastructure.eventstore.abstractions.IEvent
import java.util.*

class ReservationCanceled(override val id: UUID, val hotelId: UUID, val roomType: String): IEvent