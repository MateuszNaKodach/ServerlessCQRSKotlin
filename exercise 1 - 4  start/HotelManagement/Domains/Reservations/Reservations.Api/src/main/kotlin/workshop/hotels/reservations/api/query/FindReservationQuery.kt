package workshop.hotels.reservations.api.query

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.IQuery
import workshop.hotels.reservations.domain.models.readmodels.reservation.ReservationReadModel
import java.util.*

class FindReservationQuery(val reservationId: UUID) : IQuery<ReservationReadModel?>{
}