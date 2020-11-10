package workshop.hotels.infrastructure.cqrs.essentials.cqrs

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.*
import workshop.hotels.infrastructure.eventstore.abstractions.*
import workshop.hotels.infrastructure.storage.abstractions.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KClass

class Builder(private val repositoryFactory: IRepositoryFactory) : IBuilder {

    private val denormalizerDescriptors = mutableMapOf<String, DenormalizerDesc>()
    private val eventHandlers  = mutableMapOf<String, MutableList<Pair<String, (IEvent) -> Unit>>>()
    private val lock = ReentrantLock()

    override fun registerDenormalizer(descriptor: DenormalizerDesc) {
        val modelName: String = descriptor.readModelClazz.simpleName ?: throw NullPointerException("descriptor.readModelType::class.simpleName can't be null")

        if(modelName != "")
            denormalizerDescriptors.putIfAbsent(modelName, descriptor)
    }

    override fun <TModel: IEntity, TEvent: IEvent>registerEventHandler(readModelTypeName: String, eventTypeName: String, eventHandler: (IDenormalizerContext<TModel>, TEvent) -> Unit) {
        //if no event handlers ever registered then set default list of event handlers for the event type name
        if (!eventHandlers.containsKey(eventTypeName)){
            eventHandlers.putIfAbsent(eventTypeName, mutableListOf<Pair<String, (IEvent) -> Unit>>())
        }

        val handlers = eventHandlers.getValue(eventTypeName)

        //add event handler
        handlers.add(Pair(
                readModelTypeName,
                fun(ev: IEvent) {
                    val ctx = buildContext<TModel>(readModelTypeName)
                    @Suppress("UNCHECKED_CAST") val event = ev as? TEvent
                    event?.let {
                        eventHandler(ctx, it)
                    }
                }))
    }

    override fun handle(eventData: IEventData) {
        if (!eventHandlers.containsKey(eventData.eventType)) return

        val handlers = eventHandlers.getValue(eventData.eventType)

        lock.lock()
        try{
            for (eventHandler in handlers){
                runEventHandler(eventHandler, eventData)
            }
        }
        finally{
            lock.unlock()
        }
    }

    private fun runEventHandler(eventHandler: Pair<String, (IEvent) -> Unit>, eventData: IEventData) {
        val eventHandlerFunc = eventHandler.second
        try
        {
            eventData.event?.let {
                val event = it as IEvent
                eventHandlerFunc(event)
            }
        }
        catch (e:Exception)
        {
            //Debug.WriteLine("Read model {0} handler for {1} failed with event at {2}@{3}: {4}",
                //readModelName, eventData.EventType, eventData.EventNumber, eventData.StreamId, ex);
            //you can pass in logger and log exception as well
        }
    }

    private fun <TModel: IEntity>buildContext(readModelName: String): IDenormalizerContext<TModel>
    {
        val denormalizerDescriptor = denormalizerDescriptors.getValue(readModelName)
        @Suppress("UNCHECKED_CAST") val repository = repositoryFactory.create<TModel>(denormalizerDescriptor.readModelClazz as KClass<TModel>)
        val lookups = mutableMapOf<String, Any>()

        denormalizerDescriptor.lookups?.forEach { lookupClazz ->
            val lookupTypeName = lookupClazz.simpleName
            lookupTypeName?.let { typeName ->
                lookups.putIfAbsent(typeName, repositoryFactory.create(lookupClazz))
            }
        }

        return DenormalizerContext(repository, lookups)
    }

}