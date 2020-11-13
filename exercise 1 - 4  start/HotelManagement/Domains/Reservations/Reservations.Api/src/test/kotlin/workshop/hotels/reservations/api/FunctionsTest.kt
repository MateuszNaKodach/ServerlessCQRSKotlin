package workshop.hotels.reservations.api

import com.google.gson.Gson
import com.microsoft.azure.functions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import workshop.hotels.reservations.domain.models.commands.MakeReservation
import java.util.*
import java.util.logging.Logger
import kotlin.collections.HashMap

/**
 * Unit test for Function class.
 */
class ReservationFunctionsTest {

    private inline fun <reified T : Any> mock() = mock(T::class.java)

    private fun testMakeReservationHttpTrigger() {
        // Setup
        val req: HttpRequestMessage<String> = mock()

        val reservationId = UUID.fromString("d26b918f-5626-48a1-bf8b-18c13a118499")

        val command = MakeReservation(reservationId, UUID.randomUUID(), "Penthouse")
        val gson = Gson()
        val reqBody = gson.toJson(command)
        doReturn(reqBody).`when`<HttpRequestMessage<*>>(req).body
        doReturn(HttpMethod.POST).`when`<HttpRequestMessage<*>>(req).httpMethod

        doAnswer { invocation ->
            val status = invocation.arguments[0] as HttpStatus
            HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status)
        }.`when`<HttpRequestMessage<*>>(req).createResponseBuilder(any(HttpStatus::class.java))

        val context = mock(ExecutionContext::class.java)
        doReturn(Logger.getGlobal()).`when`(context).logger

        // Invoke
        val ret = MakeReservationFunction().run(req, context)

        // Verify
        assertEquals(ret.status, HttpStatus.OK)
    }

    private fun testGetReservationByIdHttpTrigger() {
        // Setup
        val req = mock<HttpRequestMessage<Optional<String>>>()

        val queryParams = HashMap<String, String>()
        queryParams["id"] = "d26b918f-5626-48a1-bf8b-18c13a118499"
        doReturn(HttpMethod.GET).`when`<HttpRequestMessage<*>>(req).httpMethod
        doReturn(queryParams).`when`<HttpRequestMessage<Optional<String>>>(req).queryParameters

        doAnswer { invocation ->
            val status = invocation.arguments[0] as HttpStatus
            HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status)
        }.`when`<HttpRequestMessage<*>>(req).createResponseBuilder(any(HttpStatus::class.java))

        val context = mock(ExecutionContext::class.java)
        doReturn(Logger.getGlobal()).`when`(context).logger

        // Invoke
        val ret = GetReservationByIdFunction().run(req, context)

        // Verify
        assertEquals(ret.status, HttpStatus.OK)
    }

    /**
     * Unit test for HttpTrigger POST method.
     */
    @Test
    @Throws(Exception::class)
    fun testHttpTriggerPOST() {
        testMakeReservationHttpTrigger()
    }

    /**
     * Unit test for HttpTrigger GET method.
     */
    @Test
    @Throws(Exception::class)
    fun testHttpTriggerGET() {
        testGetReservationByIdHttpTrigger()
    }
}
