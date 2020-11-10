package workshop.hotels.reservations.api

import EventStorage
import com.github.msemys.esjc.EventStoreBuilder
import org.jetbrains.exposed.sql.Database
import workshop.hotels.infrastructure.cqrs.essentials.cqrs.Builder
import workshop.hotels.infrastructure.cqrs.essentials.cqrs.Bus
import workshop.hotels.infrastructure.cqrs.essentials.ddd.AggregateFactory
import workshop.hotels.infrastructure.cqrs.essentials.es.EventStoreClient
import workshop.hotels.infrastructure.storage.sqlite.RepositoryFactory
import workshop.hotels.reservations.api.handlers.command.MakeReservationCommandHandler
import workshop.hotels.reservations.api.handlers.query.FindReservationQueryHandler
import workshop.hotels.reservations.api.handlers.query.FindRoomTypeAvailabilityQueryHandler
import workshop.hotels.reservations.api.query.FindReservationQuery
import workshop.hotels.reservations.api.query.FindRoomTypeAvailabilityQuery
import workshop.hotels.reservations.domain.aggregates.Reservation
import workshop.hotels.reservations.domain.models.commands.MakeReservation
import workshop.hotels.reservations.domain.models.readmodels.reservation.ReservationDenormalizer
import workshop.hotels.reservations.domain.models.readmodels.reservation.ReservationReadModel
import workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability.RoomTypeAvailabilityDenormalizer
import workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability.RoomTypeAvailabilityReadModel
import java.time.Duration

abstract class FunctionBase {

    val bus: Bus

    init{
        //for the sake of keeping the workshop initialize dependencies in here instead of DI
        //NOTE all connections should only be made and stored once across all calls for a production scenario
        val esConnection = EventStoreBuilder.newBuilder()
                .singleNodeAddress("localhost", 1113)
                .userCredentials("admin", "changeit")
                .heartbeatInterval(Duration.ofMillis(1000))
                .heartbeatTimeout(Duration.ofMillis(5000))
                .build()

        val eventStore = EventStorage(esConnection)

        bus = Bus()
        val eventStoreClient = EventStoreClient(bus, eventStore)
        //command handlers registration
        val commandHandler = MakeReservationCommandHandler(AggregateFactory(Reservation::class, eventStoreClient), eventStoreClient)
        bus.registerCommandHandler(MakeReservation::class.simpleName, commandHandler::handle)
        //db connection set up (for workshop just instantiate here but production code should only make one connection across all calls
        val database = Database.connect("jdbc:sqlite:./../../../reservation.db", "org.sqlite.JDBC")
        //denormalizer context builder
        val repositoryFactory = RepositoryFactory(database)
        val builder = Builder(repositoryFactory)
        //read model/denormalizers registration
        ReservationDenormalizer(builder)
        RoomTypeAvailabilityDenormalizer(builder)
        bus.registerEventHandler { eventData -> builder.handle(eventData) }
        //query handlers registration
        val findReservationQueryHandler = FindReservationQueryHandler(repositoryFactory)
        bus.registerQueryHandler<FindReservationQuery, ReservationReadModel?>(FindReservationQuery::class.simpleName, findReservationQueryHandler::handle)

        val findRoomTypeAvailabilityQueryHandler = FindRoomTypeAvailabilityQueryHandler(repositoryFactory)
        bus.registerQueryHandler<FindRoomTypeAvailabilityQuery, RoomTypeAvailabilityReadModel?>(FindRoomTypeAvailabilityQuery::class.simpleName, findRoomTypeAvailabilityQueryHandler::handle)
    }
}