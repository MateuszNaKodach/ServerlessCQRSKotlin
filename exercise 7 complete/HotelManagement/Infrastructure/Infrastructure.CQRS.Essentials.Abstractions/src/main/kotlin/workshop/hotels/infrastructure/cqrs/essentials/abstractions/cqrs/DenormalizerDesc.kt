package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

import kotlin.reflect.KClass

class DenormalizerDesc(val readModelClazz: KClass<*>, val lookups: Array<KClass<*>>? = null) {

}