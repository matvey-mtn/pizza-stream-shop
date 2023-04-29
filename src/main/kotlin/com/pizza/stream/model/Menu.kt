package com.pizza.stream.model

typealias Price = Double
data class Menu(
    val prices: Map<Pair<PizzaType, PizzaSize>, Price>
)
