package workshop.hotels.infrastructure.storage.sqlite

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import workshop.hotels.infrastructure.storage.abstractions.IEntity
import workshop.hotels.infrastructure.storage.abstractions.IRepository
import java.sql.ResultSet
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

private const val ID_KEY = "id"

class Repository<TEntity: IEntity>(private val database: Database, private val entityClazz: KClass<TEntity>) : IRepository<TEntity> {

    override fun get(): List<TEntity> {
        val entities = mutableListOf<TEntity>()
        val table = getTable(entityClazz.simpleName!!)
        //build up a query and execute then map it to an entities to be returned
        transaction(database) {
            if(table.exists()) {
                val conn = TransactionManager.current().connection
                val query = "select * from ${entityClazz.simpleName}"
                val statement = conn.prepareStatement(query, false)
                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    entities.add(mapToEntity(resultSet))
                }
            }
        }
        return entities.toList()
    }

    override fun get(id: UUID): TEntity? {
        return get(id.toString())
    }

    override fun get(id: String): TEntity? {
        var entity: TEntity? = null
        val table = getTable(entityClazz.simpleName!!)
        //build up a query and execute then map it to an entity to be returned
        transaction(database) {
            if(table.exists()) {
                val conn = TransactionManager.current().connection
                val query = "select * from ${entityClazz.simpleName} where $ID_KEY = ?"
                val statement = conn.prepareStatement(query, false)
                statement.fillParameters(listOf(Pair(VarCharColumnType(), id)))
                val resultSet = statement.executeQuery()
                if (resultSet.next())
                    entity = mapToEntity(resultSet)
            }
        }
        return entity
    }

    override fun save(entity: TEntity){
        //get the entity properties and values using reflection
        val entityProperties = mutableMapOf<String, Any?>()
        entityClazz.memberProperties.forEach {
            if (it.visibility == KVisibility.PUBLIC) {
                val value = it.getter.call(entity)
                entityProperties.putIfAbsent(it.name, value)
            }
        }
        //build a table dynamically using the entity property keys
        val table = getTable(entityClazz.simpleName!!)
        transaction {
            SchemaUtils.create(table) //if table doesn't exist create it
        }
        //get existing entity if found do update otherwise do insert
        val existingEntity = get(entity.id)
        if(existingEntity == null){
            insert(table, entityProperties)
        }else{
            update(existingEntity, table, entityProperties)
        }
    }

    private fun insert(table: IdTable<String>, entityProperties: MutableMap<String, Any?>) {
        //create table if not exists and insert for each table column the matching entity property values
        transaction {
            table.insertIgnore {
                for(column in table.columns){
                    @Suppress("UNCHECKED_CAST")
                    it[column as Column<Any>] = entityProperties[column.name].toString()
                }
            }
        }
    }

    private fun update(entity: TEntity, table: IdTable<String>, entityProperties: MutableMap<String, Any?>) {
        //create table if not exists and insert for each table column the matching entity property values
        transaction {
            table.update({ table.id eq entity.id}) {
                for(column in table.columns){
                    @Suppress("UNCHECKED_CAST")
                    it[column as Column<Any>] = entityProperties[column.name].toString()
                }
            }
        }
    }

    //generate a table dynamically and return it
    private fun getTable(tableName: String): IdTable<String> {

        val columnNames = entityClazz.memberProperties.map{p -> p.name}

        return object : IdTable<String>(tableName) {
            val idColumn = varchar(ID_KEY, 50).uniqueIndex()
            override val id: Column<EntityID<String>> = idColumn.entityId()
            override val columns: List<Column<*>>
                get() = getTableColumns(this, columnNames)
            override val primaryKey = PrimaryKey(idColumn, name="PK_ID")
        }
    }

    //generate table columns dynamically and return them
    private fun getTableColumns(table: Table, columnsToRegister: List<String>): List<Column<*>>{
        val columns = mutableListOf<Column<*>>()
        for(columnToRegister in columnsToRegister){
            columns.add(Column<VarCharColumnType>(table, columnToRegister, VarCharColumnType()))
        }
        return columns.toList()
    }

    //map query result set to an entity using reflection
    private fun mapToEntity(resultSet: ResultSet): TEntity{
        val entityId = resultSet.getString(ID_KEY)
        val entity = entityClazz.constructors.first().call(entityId)
        entity::class.memberProperties.forEach {
            if (it is KMutableProperty<*>) {
                val resultSetValue = resultSet.getString(it.name)
                when(it.returnType.classifier){
                    UUID::class -> it.setter.call(entity, UUID.fromString(resultSetValue))
                    Boolean::class -> it.setter.call(entity, resultSetValue.toBoolean())
                    Double::class -> it.setter.call(entity, resultSetValue.toDouble())
                    Float::class -> it.setter.call(entity, resultSetValue.toFloat())
                    Long::class -> it.setter.call(entity, resultSetValue.toLong())
                    Int::class -> it.setter.call(entity, resultSetValue.toInt())
                    Short::class -> it.setter.call(entity, resultSetValue.toShort())
                    Byte::class -> it.setter.call(entity, resultSetValue.toByte())
                    //ULong::class -> it.setter.call(entity, resultSetValue.toULong())
                    //UInt::class -> it.setter.call(entity, resultSetValue.toUInt())
                    //UShort::class -> it.setter.call(entity, resultSetValue.toUShort())
                    //UByte::class -> it.setter.call(entity, resultSetValue.toUByte())
                    Date::class -> it.setter.call(entity, Date.from(Instant.parse(resultSetValue)))
                    CharArray::class -> it.setter.call(entity, resultSetValue.toCharArray())
                    String::class -> it.setter.call(entity, resultSetValue)
                }
            }
        }
        return entity
    }
}