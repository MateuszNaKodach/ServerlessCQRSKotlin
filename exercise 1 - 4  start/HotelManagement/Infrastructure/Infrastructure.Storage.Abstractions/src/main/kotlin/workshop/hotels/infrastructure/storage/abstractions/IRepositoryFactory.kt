package workshop.hotels.infrastructure.storage.abstractions

import kotlin.reflect.KClass

interface IRepositoryFactory {
    fun <TEntity: IEntity>create(entityClazz: KClass<TEntity>): IRepository<TEntity>
    fun create(entityClazz: KClass<*>): Any
}