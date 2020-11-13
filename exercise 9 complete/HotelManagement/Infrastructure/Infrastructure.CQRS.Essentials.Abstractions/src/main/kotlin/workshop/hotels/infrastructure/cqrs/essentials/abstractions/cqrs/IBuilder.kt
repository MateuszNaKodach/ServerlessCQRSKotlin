package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

import workshop.hotels.infrastructure.eventstore.abstractions.*
import workshop.hotels.infrastructure.storage.abstractions.IEntity

interface IBuilder {
    fun registerDenormalizer(descriptor: DenormalizerDesc)
    fun <TModel: IEntity, TEvent: IEvent>registerEventHandler(readModelTypeName: String, eventTypeName: String, eventHandler: (IDenormalizerContext<TModel>, TEvent) -> Unit)
    fun handle(eventData: IEventData)
}