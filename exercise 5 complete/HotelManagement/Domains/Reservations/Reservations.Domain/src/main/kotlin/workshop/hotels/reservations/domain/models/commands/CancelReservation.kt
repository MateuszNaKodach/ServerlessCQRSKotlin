package workshop.hotels.reservations.domain.models.commands

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.ICommand
import java.util.*

class CancelReservation(override val id: UUID, val hotelId: UUID, val roomType: String) : ICommand{
}