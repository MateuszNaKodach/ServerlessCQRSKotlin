package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

import workshop.hotels.infrastructure.eventstore.abstractions.*

interface IBus {
    fun <TCommand: ICommand>send(command: TCommand): Array<IEvent>?
    fun <TCommand: ICommand>registerCommandHandler(commandHandlerKey: String?, commandHandler: (TCommand) -> Array<IEvent>)
    fun <TQuery, TResult>send(query: TQuery): TResult
    fun <TQuery, TResult>registerQueryHandler(queryHandlerKey: String?, queryHandler: (TQuery) -> TResult?)
    fun registerEventHandler(handler: (IEventData) -> Unit)
    fun <TEvent: IEvent>publish(event: TEvent)
    fun publish(eventData: IEventData)
}