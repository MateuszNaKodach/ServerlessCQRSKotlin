package workshop.hotels.infrastructure.cqrs.essentials.abstractions.cqrs

interface IQueryHandler<in TQuery, out TResult> where TQuery : IQuery<TResult> {
    fun handle(query:TQuery): TResult
}