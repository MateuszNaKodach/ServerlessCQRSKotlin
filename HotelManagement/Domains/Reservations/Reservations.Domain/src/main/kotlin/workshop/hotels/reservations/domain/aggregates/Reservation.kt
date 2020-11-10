package workshop.hotels.reservations.domain.aggregates

import workshop.hotels.reservations.domain.models.commands.MakeReservation
import workshop.hotels.reservations.domain.models.events.ReservationMade
import workshop.hotels.infrastructure.cqrs.essentials.ddd.AggregateBase
import workshop.hotels.infrastructure.eventstore.abstractions.*
import workshop.hotels.reservations.domain.models.events.ReservationNotMade
import java.util.*

class Reservation : AggregateBase<Reservation.State>(State())
{
    class State
    {
        var id: UUID? = null
        var hotelId: UUID? = null
        var roomType: String? = null
        var isReserved: Boolean = false
        var reason: String? = null
    }

    init{
        registerTransition(MakeReservation::class, fun(state: State, event: ReservationMade): State {
            return apply(state, event)
        })
    }

    fun makeReservation(command: MakeReservation): Array<IEvent> {
        if((command.roomTypeAvailableAmount-1) <= 0) {
            raiseEvent(ReservationNotMade(command.id, command.hotelId, command.roomType, "Room Type Unavailable"))
        }else {
            raiseEvent(ReservationMade(command.id, command.hotelId, command.roomType))
        }
        return unCommitedEvents.toTypedArray()
    }

    private fun apply(state: State, event: ReservationMade): State {
        state.id = event.id
        state.hotelId = event.hotelId
        state.isReserved = true
        state.roomType = event.roomType
        return state
    }

    private fun apply(state: State, event: ReservationNotMade): State {
        state.id = event.id
        state.hotelId = event.hotelId
        state.isReserved = false
        state.roomType = event.roomType
        state.reason = event.reason
        return state
    }
}