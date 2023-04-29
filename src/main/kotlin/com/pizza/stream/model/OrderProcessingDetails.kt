package com.pizza.stream.model

class OrderProcessingDetails(
    val order: Order,
    val pizza: Pizza,
    val ingredientsAdjustedToSize: Map<Ingredient, Amount>,
    var status: Status = Status.ASSEMBLED
)

enum class Status {
    ASSEMBLED,
    BACKED,
    PACKAGED,
    COMPLETED
}
