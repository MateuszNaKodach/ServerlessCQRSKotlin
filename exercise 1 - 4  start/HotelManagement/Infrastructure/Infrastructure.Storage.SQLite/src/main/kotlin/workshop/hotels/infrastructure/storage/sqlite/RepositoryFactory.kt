package workshop.hotels.infrastructure.storage.sqlite

import org.jetbrains.exposed.sql.Database
import workshop.hotels.infrastructure.storage.abstractions.*
import kotlin.reflect.KClass

class RepositoryFactory(private val database: Database) : IRepositoryFactory {

    override fun <TEntity: IEntity> create(entityClazz: KClass<TEntity>): IRepository<TEntity> {
        return Repository(database, entityClazz)
    }

    override fun create(entityClazz: KClass<*>): Any {

        val constructor = entityClazz.constructors.first { it.parameters.isEmpty() }

        val arguments = constructor.parameters
                .map { it.type.classifier as KClass<*> }
                .map { create(it) }
                .toTypedArray()

        return constructor.call(*arguments)
    }
}