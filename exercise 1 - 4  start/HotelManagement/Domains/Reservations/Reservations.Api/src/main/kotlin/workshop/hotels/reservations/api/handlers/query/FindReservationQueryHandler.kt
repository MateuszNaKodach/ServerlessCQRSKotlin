package workshop.hotels.reservations.api.handlers.query

import workshop.hotels.reservations.api.query.FindReservationQuery
import workshop.hotels.reservations.domain.models.readmodels.reservation.ReservationReadModel
import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.IQueryHandler
import workshop.hotels.infrastructure.storage.abstractions.IRepositoryFactory

class FindReservationQueryHandler(private val repositoryFactory: IRepositoryFactory) : IQueryHandler<FindReservationQuery, ReservationReadModel?>
{
    override fun handle(query: FindReservationQuery): ReservationReadModel?
    {
        val repository = repositoryFactory.create<ReservationReadModel>(ReservationReadModel::class)
        return repository.get(query.reservationId)
    }
}