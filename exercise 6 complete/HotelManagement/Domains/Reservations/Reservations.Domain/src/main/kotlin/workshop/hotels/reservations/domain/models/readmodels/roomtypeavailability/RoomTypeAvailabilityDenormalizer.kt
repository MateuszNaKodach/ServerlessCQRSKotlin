package workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability

import workshop.hotels.reservations.domain.models.events.*
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.*

class RoomTypeAvailabilityDenormalizer(private val builder: IBuilder) {

    init{
        builder.registerDenormalizer(DenormalizerDesc(RoomTypeAvailabilityReadModel::class))
        builder.registerEventHandler(RoomTypeAvailabilityReadModel::class.simpleName!!, ReservationMade::class.simpleName!!, this::onReservationMade) //virtual workshop ex-8 hint
    }

    //virtual workshop ex-8 hint
    private fun onReservationMade(ctx: IDenormalizerContext<RoomTypeAvailabilityReadModel>, event: ReservationMade)
    {
        val roomTypeAvailablity = ctx.repository.get(event.roomType) ?: RoomTypeAvailabilityReadModel(event.roomType)
        roomTypeAvailablity.hotelId = event.hotelId
        roomTypeAvailablity.roomType = event.roomType
        roomTypeAvailablity.amount -= 1
        ctx.repository.save(roomTypeAvailablity)
    }
}