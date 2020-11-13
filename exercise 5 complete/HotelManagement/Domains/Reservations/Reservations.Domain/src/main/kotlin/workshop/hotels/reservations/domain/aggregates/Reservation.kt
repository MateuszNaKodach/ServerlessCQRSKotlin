package workshop.hotels.reservations.domain.aggregates

import workshop.hotels.reservations.domain.models.commands.*
import workshop.hotels.reservations.domain.models.events.*
import workshop.hotels.infrastructure.cqrs.essentials.ddd.AggregateBase
import workshop.hotels.infrastructure.eventstore.abstractions.*
import java.util.*

class Reservation : AggregateBase<Reservation.State>(State())
{
    class State
    {
        var id: UUID? = null
        var hotelId: UUID? = null
        var roomType: String? = null
        var isReserved: Boolean = false //virtual-workshop ex-7 hint
        var reason: String? = null
    }

    init{
        registerTransition(MakeReservation::class, fun(state: State, event: ReservationMade): State {
            return apply(state, event)
        }) //virtual-workshop ex-7 hint
    }

    //virtual-workshop ex-7 hint
    fun makeReservation(command: MakeReservation): Array<IEvent> {
        if(command.roomTypeAvailableAmount > 0) {
            raiseEvent(ReservationMade(command.id, command.hotelId, command.roomType))
        }else {
            raiseEvent(ReservationNotMade(command.id, command.hotelId, command.roomType, "Room Type Unavailable"))
        }
        return unCommitedEvents.toTypedArray()
    }

    private fun apply(state: State, event: ReservationMade): State {
        state.id = event.id
        state.hotelId = event.hotelId
        state.isReserved = true //virtual-workshop ex-7 hint
        state.roomType = event.roomType
        return state
    }

    private fun apply(state: State, event: ReservationNotMade): State {
        state.id = event.id
        state.hotelId = event.hotelId
        state.isReserved = false //virtual-workshop ex-7 hint
        state.roomType = event.roomType
        state.reason = event.reason
        return state
    }
}