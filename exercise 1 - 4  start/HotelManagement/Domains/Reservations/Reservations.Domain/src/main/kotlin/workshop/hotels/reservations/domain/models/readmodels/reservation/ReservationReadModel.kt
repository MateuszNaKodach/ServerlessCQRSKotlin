package workshop.hotels.reservations.domain.models.readmodels.reservation

import workshop.hotels.infrastructure.storage.abstractions.IEntity
import java.util.*

data class ReservationReadModel(override val id: String): IEntity{
    var hotelId: UUID? = null
    var roomType: String? = null
}