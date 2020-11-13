package workshop.hotels.infrastructure.eventstore.abstractions

interface IMetadataProvider {
    fun <T> tryGet(name: String): Pair<Boolean, T?>
}