package workshop.hotels.reservations.domain.models.commands

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.ICommand
import java.util.*

//virtual workshop ex-5 hint
class MakeReservation(override val id: UUID, val hotelId: UUID, val roomType: String) : ICommand{
    var roomTypeAvailableAmount: Int = 0
}