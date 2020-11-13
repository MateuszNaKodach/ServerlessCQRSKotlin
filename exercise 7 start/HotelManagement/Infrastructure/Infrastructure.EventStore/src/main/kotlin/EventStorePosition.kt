import com.github.msemys.esjc.Position
import workshop.hotels.infrastructure.eventstore.abstractions.IPosition

class EventStorePosition(val position: Position) : IPosition
{
    override fun compareTo(other: IPosition): Int {
        if (other !is EventStorePosition) throw IllegalArgumentException("other must be a EventStorePosition");
        var right = other;
        if (position < right.position) return -1;
        if (position > right.position) return 1;
        return 0;
    }
}