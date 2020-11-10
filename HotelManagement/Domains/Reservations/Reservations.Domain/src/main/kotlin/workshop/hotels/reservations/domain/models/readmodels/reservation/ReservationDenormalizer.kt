package workshop.hotels.reservations.domain.models.readmodels.reservation

import workshop.hotels.reservations.domain.models.events.ReservationMade
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.*

class ReservationDenormalizer(private val builder: IBuilder) {

    init{
        builder.registerDenormalizer(DenormalizerDesc(ReservationReadModel::class))
        builder.registerEventHandler(ReservationReadModel::class.simpleName!!, ReservationMade::class.simpleName!!, this::onReservationMade)
    }

    private fun onReservationMade(ctx: IDenormalizerContext<ReservationReadModel>, event: ReservationMade)
    {
        val reservation = ReservationReadModel(event.id.toString())
        reservation.hotelId = event.hotelId
        reservation.roomType = event.roomType
        ctx.repository.save(reservation)
    }
}