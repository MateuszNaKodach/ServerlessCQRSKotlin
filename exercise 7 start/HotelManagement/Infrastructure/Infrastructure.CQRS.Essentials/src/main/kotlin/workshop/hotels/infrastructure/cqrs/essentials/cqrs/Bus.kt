package workshop.hotels.infrastructure.cqrs.essentials.cqrs

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.*
import workshop.hotels.infrastructure.eventstore.abstractions.*
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class Bus(): IBus {

    private val commandHandlers = mutableMapOf<String, (ICommand) -> Array<IEvent>>()
    private val queryHandlers = mutableMapOf<String, (Any) -> Any?>()
    private val eventHandlers = mutableListOf<(IEventData) -> Unit>()

    override fun <TCommand : ICommand> send(command: TCommand): Array<IEvent>? {
        val typeName = command::class.simpleName ?: ""
        if(!commandHandlers.containsKey(typeName))
            throw IllegalArgumentException("There is no command handler registered for command $typeName")

        val handler = commandHandlers.getValue(typeName)
        return handler(command)
    }

    override fun <TCommand: ICommand>registerCommandHandler(commandHandlerKey: String?, commandHandler: (TCommand) -> Array<IEvent>)
    {
        val typeName = commandHandlerKey ?: ""
        commandHandlers.putIfAbsent(typeName, fun(command: ICommand): Array<IEvent> { return commandHandler(command as TCommand) })
    }

    override fun <TQuery, TResult> send(query: TQuery): TResult {
        val typeName = query!!::class.simpleName ?: ""
        if(!queryHandlers.containsKey(typeName))
            throw IllegalArgumentException("There is no query handler registered for query $typeName")

        val handler = queryHandlers.getValue(typeName)
        return handler(query) as TResult
    }

    override fun <TQuery, TResult>registerQueryHandler(queryHandlerKey: String?, queryHandler: (TQuery) -> TResult?)
    {
        val typeName = queryHandlerKey ?: ""
        queryHandlers.putIfAbsent(typeName, fun(query: Any): Any? { return queryHandler(query as TQuery) })
    }

    override fun registerEventHandler(handler: (IEventData) -> Unit) {
        eventHandlers.add(handler)
    }

    override fun publish(eventData: IEventData) {
        //Publish must never throw
        try {
            for (handler in eventHandlers) {
                handler(eventData)
            }
        }
        catch (e: Exception) {
            //log it!!
            //"Publish failed"
        }
    }

    override fun <TEvent: IEvent> publish(event: TEvent) {
        //set additional properties used for publishing
        val eventData = EventData()
        eventData.eventType = event::class.simpleName ?: ""
        eventData.event = event
        eventData.metadata = UserMetadata()
        //publish event data
        publish(eventData)
    }
}