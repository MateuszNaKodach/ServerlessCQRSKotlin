package workshop.hotels.reservations.domain.models.readmodels.reservation

import workshop.hotels.reservations.domain.models.events.*
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.*

class ReservationDenormalizer(private val builder: IBuilder) {

    init{
        builder.registerDenormalizer(DenormalizerDesc(ReservationReadModel::class))
        builder.registerEventHandler(ReservationReadModel::class.simpleName!!, ReservationMade::class.simpleName!!, this::onReservationMade)  //virtual workshop ex-8 hint
    }

    //virtual workshop ex-8 hint
    private fun onReservationMade(ctx: IDenormalizerContext<ReservationReadModel>, event: ReservationMade)
    {
        val reservation = ReservationReadModel(event.id.toString())
        reservation.hotelId = event.hotelId
        reservation.roomType = event.roomType
        reservation.isReserved = true
        ctx.repository.save(reservation)
    }
}