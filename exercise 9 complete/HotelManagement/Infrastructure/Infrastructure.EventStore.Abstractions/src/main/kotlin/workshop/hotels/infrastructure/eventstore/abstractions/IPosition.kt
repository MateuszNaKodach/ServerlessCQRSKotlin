package workshop.hotels.infrastructure.eventstore.abstractions

interface IPosition {
    fun compareTo(other: IPosition): Int
}