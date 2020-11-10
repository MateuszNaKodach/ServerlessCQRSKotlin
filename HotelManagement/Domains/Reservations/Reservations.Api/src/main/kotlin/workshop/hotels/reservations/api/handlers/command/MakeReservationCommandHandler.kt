package workshop.hotels.reservations.api.handlers.command

import workshop.hotels.reservations.domain.aggregates.*
import workshop.hotels.reservations.domain.models.commands.MakeReservation
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.ICommandHandler
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd.IAggregateFactory
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.es.IEventStoreClient
import workshop.hotels.infrastructure.eventstore.abstractions.*

class MakeReservationCommandHandler(private val reservationFactory: IAggregateFactory<Reservation>, private val eventStoreClient: IEventStoreClient): ICommandHandler<MakeReservation> {
    override fun handle(command: MakeReservation): Array<IEvent> {
        val reservationId = command.id
        val reservation = reservationFactory.get(reservationId)
        val events = reservation.makeReservation(command)
        eventStoreClient.save(reservation, reservationId)
        return events
    }
}