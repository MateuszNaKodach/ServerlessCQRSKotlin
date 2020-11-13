package workshop.hotels.infrastructure.cqrs.essentials.cqrs

import workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs.*
import workshop.hotels.infrastructure.storage.abstractions.*
import kotlin.reflect.KClass

class DenormalizerContext<TModel: IEntity>(override val repository: IRepository<TModel>, override val lookups: MutableMap<String, Any>): IDenormalizerContext<TModel> {
    override fun <TLookup: IEntity> lookup(clazz:KClass<TLookup>): IRepository<TLookup> {
        val lookUpTypeName = clazz.qualifiedName ?: ""
        @Suppress("UNCHECKED_CAST")
        return lookups.getValue(lookUpTypeName) as IRepository<TLookup>
    }
}