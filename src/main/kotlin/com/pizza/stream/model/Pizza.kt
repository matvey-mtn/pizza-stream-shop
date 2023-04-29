package com.pizza.stream.model

class Pizza(
    val name: String = "",
    val pizzaType: PizzaType,
    val size: PizzaSize,
    var isReady: Boolean = false
)

enum class PizzaSize {
    SMALL,
    MEDIUM,
    LARGE
}

enum class PizzaType {
    MARGHERITA,
    PEPPERONI,
    VEGGIE,
    HAWAIIAN
}
