package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

import workshop.hotels.infrastructure.eventstore.abstractions.*

interface ICommandHandler<in T: ICommand> {
    fun handle(command: T): Array<IEvent>
}