package workshop.hotels.reservations.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import workshop.hotels.reservations.api.query.FindRoomTypeAvailabilityQuery
import workshop.hotels.reservations.domain.models.commands.MakeReservation
import workshop.hotels.reservations.domain.models.readmodels.roomtypeavailability.RoomTypeAvailabilityReadModel

/**
 * Azure Functions with HTTP Trigger.
 */
//virtual workshop ex-9 hint
class MakeReservationFunction : FunctionBase(){

    @FunctionName("MakeReservationFunction")
    fun run(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.POST],
            route = "reservation/make",
            authLevel = AuthorizationLevel.FUNCTION) request: HttpRequestMessage<String>,
        context: ExecutionContext): HttpResponseMessage {

        context.logger.info("HTTP trigger processed a ${request.httpMethod.name} request.")

        try {
            val command:MakeReservation = Gson().fromJson(request.body, object : TypeToken<MakeReservation>() {}.type)
            //get and set room type amount for check
            var roomTypeAvailabilityReadModel = bus.send<FindRoomTypeAvailabilityQuery, RoomTypeAvailabilityReadModel?>(FindRoomTypeAvailabilityQuery(command.roomType))
            command.roomTypeAvailableAmount = roomTypeAvailabilityReadModel?.amount ?: 0
            //fire off command
            val events = bus.send(command)
            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .body(events)
                    .build()

        }catch(e:Exception){
            return request
                    .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build()
        }
    }

}
