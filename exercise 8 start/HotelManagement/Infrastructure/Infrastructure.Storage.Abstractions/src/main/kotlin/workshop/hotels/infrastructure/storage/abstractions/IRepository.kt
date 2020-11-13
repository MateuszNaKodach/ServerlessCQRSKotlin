package workshop.hotels.infrastructure.storage.abstractions

import java.util.*

interface IRepository<TEntity: IEntity>{
    fun get(): List<TEntity>
    fun get(id: UUID): TEntity?
    fun get(id: String): TEntity?
    fun save(entity: TEntity)
}