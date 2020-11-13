package workshop.hotels.reservations.api.handlers.query

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.IQueryHandler
import workshop.hotels.infrastructure.storage.abstractions.IRepositoryFactory
import workshop.hotels.reservations.api.query.FindRoomTypeAvailabilityQuery
import workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability.RoomTypeAvailabilityReadModel

class FindRoomTypeAvailabilityQueryHandler(private val repositoryFactory: IRepositoryFactory) : IQueryHandler<FindRoomTypeAvailabilityQuery, RoomTypeAvailabilityReadModel?>
{
    override fun handle(query: FindRoomTypeAvailabilityQuery): RoomTypeAvailabilityReadModel?
    {
        val repository = repositoryFactory.create<RoomTypeAvailabilityReadModel>(RoomTypeAvailabilityReadModel::class)
        return repository.get(query.roomType)
    }
}