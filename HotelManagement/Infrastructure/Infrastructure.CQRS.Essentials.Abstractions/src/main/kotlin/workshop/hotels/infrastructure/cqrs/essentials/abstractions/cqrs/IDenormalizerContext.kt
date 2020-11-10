package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

import workshop.hotels.infrastructure.storage.abstractions.*
import kotlin.reflect.KClass

interface IDenormalizerContext<TModel: IEntity> {
    val repository: IRepository<TModel>
    val lookups: MutableMap<String, Any>
    fun <TLookup: IEntity> lookup(clazz:KClass<TLookup>): IRepository<TLookup>
}