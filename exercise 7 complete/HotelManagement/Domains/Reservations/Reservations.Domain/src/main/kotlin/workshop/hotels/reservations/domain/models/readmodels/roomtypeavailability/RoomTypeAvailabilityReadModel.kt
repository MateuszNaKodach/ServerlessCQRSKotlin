package workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability

import workshop.hotels.infrastructure.storage.abstractions.IEntity
import java.util.*

data class RoomTypeAvailabilityReadModel(override val id: String): IEntity{
    var hotelId: UUID? = null
    var roomType: String? = null
    var amount: Int = 0
}