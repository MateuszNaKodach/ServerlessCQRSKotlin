package workshop.hotels.reservations.domain.models.commands

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.ICommand
import java.util.*

class MakeReservation(override val id: UUID, val hotelId: UUID, val roomType: String) : ICommand{
    var roomTypeAvailableAmount: Int = 0
}