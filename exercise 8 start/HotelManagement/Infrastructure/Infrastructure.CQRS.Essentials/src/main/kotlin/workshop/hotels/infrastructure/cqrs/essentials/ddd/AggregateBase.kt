package workshop.hotels.infrastructure.cqrs.essentials.ddd

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.ddd.IAggregate
import workshop.hotels.infrastructure.eventstore.abstractions.*
import kotlin.reflect.KClass

abstract class AggregateBase<TState: Any>(private var state: TState) : IAggregate
{
    override var version: Long = -1
    private val transitionRoutes = mutableMapOf<KClass<*>, (TState, IEvent) -> TState>()
    override val unCommitedEvents = mutableListOf<IEvent>()

    fun <TEvent: IEvent>registerTransition(clazz: KClass<*>, transition: (TState, TEvent) -> TState) {
        transitionRoutes.putIfAbsent(clazz, fun(state: TState, event: IEvent): TState {
            @Suppress("UNCHECKED_CAST")
            return transition(state, event as TEvent)
        })
    }

    protected fun raiseEvent(event: IEvent) {
        applyEvent(state, event, event::class)
        unCommitedEvents.add(event)
    }

    override fun clearUnCommitedEvents() {
        unCommitedEvents.clear()
    }

    private fun applyEvent(state: TState, event: IEvent, clazz: KClass<*>) {
        if (transitionRoutes.containsKey(clazz))
        {
            val transitionRoute = transitionRoutes.getValue(clazz)
            this.state = transitionRoute.invoke(state, event)
        }
        version++
    }

    override fun hydrate(event: IEvent) {
        applyEvent(state, event, event::class)
    }
}