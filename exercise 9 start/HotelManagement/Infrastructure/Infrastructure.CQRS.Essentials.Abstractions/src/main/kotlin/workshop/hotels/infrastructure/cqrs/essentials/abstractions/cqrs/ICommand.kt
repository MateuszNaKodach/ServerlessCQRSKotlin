package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

import java.util.*

interface ICommand {
    val id: UUID
}