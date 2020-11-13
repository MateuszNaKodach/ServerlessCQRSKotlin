package workshop.hotels.reservations.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import workshop.hotels.reservations.domain.models.commands.*

/**
 * Azure Functions with HTTP Trigger.
 */
class CancelReservationFunction : FunctionBase(){

    @FunctionName("CancelReservationFunction")
    fun run(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.POST],
            route = "reservation/cancel",
            authLevel = AuthorizationLevel.FUNCTION) request: HttpRequestMessage<String>,
        context: ExecutionContext): HttpResponseMessage {

        context.logger.info("HTTP trigger processed a ${request.httpMethod.name} request.")

        try {
            val command:CancelReservation = Gson().fromJson(request.body, object : TypeToken<CancelReservation>() {}.type)
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
