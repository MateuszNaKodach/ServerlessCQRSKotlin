package workshop.hotels.reservations.api

import java.util.*
import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import workshop.hotels.reservations.api.query.FindReservationQuery
import workshop.hotels.reservations.domain.models.readmodels.reservation.ReservationReadModel

/**
 * Azure Functions with HTTP Trigger.
 */
class GetReservationByIdFunction : FunctionBase() {

    @FunctionName("GetReservationByIdFunction")
    fun run(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.GET],
            route = "reservation",
            authLevel = AuthorizationLevel.FUNCTION) request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext): HttpResponseMessage {

        context.logger.info("HTTP trigger processed a ${request.httpMethod.name} request.")

        try {
            if(request.queryParameters["id"] == null)
                return request
                        .createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .build()

            val id = UUID.fromString(request.queryParameters["id"])
            val query = FindReservationQuery(id)
            val result = bus.send<FindReservationQuery, ReservationReadModel?>(query)
                    ?: return request
                            .createResponseBuilder(HttpStatus.OK)
                            .build()

            return request
                .createResponseBuilder(HttpStatus.OK)
                .body(result)
                .build()
        }
        catch(e:Exception) {
            return request
                .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .build()
        }
    }

}