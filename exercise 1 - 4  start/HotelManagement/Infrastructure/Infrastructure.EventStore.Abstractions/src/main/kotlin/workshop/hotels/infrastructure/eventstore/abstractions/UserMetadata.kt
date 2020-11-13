package workshop.hotels.infrastructure.eventstore.abstractions

class UserMetadata(private val properties: MutableMap<String, Any>): IMetadataProvider {

    constructor() : this(mutableMapOf<String, Any>())

    //GETTER PROPERTY
    val value: MutableMap<String, Any>
        get() = properties

    fun add(key: String, value: Any){
        properties.putIfAbsent(key, value)
    }

    override fun <T> tryGet(name: String): Pair<Boolean, T?> {
        if(!properties.containsKey(name)) return Pair(false, null)
        @Suppress("UNCHECKED_CAST") val returnVal = properties[name] as T
        return Pair(true, returnVal)
    }
}