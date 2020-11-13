package workshop.hotels.reservations.api.query

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.IQuery
import workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability.RoomTypeAvailabilityReadModel

class FindRoomTypeAvailabilityQuery(val roomType: String) : IQuery<RoomTypeAvailabilityReadModel?>{
}