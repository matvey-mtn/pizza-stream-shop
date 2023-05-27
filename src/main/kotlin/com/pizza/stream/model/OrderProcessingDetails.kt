package com.pizza.stream.model

class OrderProcessingDetails(
    val order: Order,
    val pizza: Pizza,
    val ingredients: Map<Name, AmountInGrams>,
    var status: Status = Status.PENDING
)

enum class Status {
    PENDING,
    ASSEMBLED,
    BACKED,
    PACKAGED,
    COMPLETED
}
