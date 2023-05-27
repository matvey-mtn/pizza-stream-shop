package com.pizza.stream.model.aggregations

import com.pizza.stream.model.PizzaSize
import com.pizza.stream.model.PizzaType

class Analytics(
    var ordersCount: Int,
    var ordersPerPizza: HashMap<PizzaType, Int>,
    var ordersPerPizzaSize: HashMap<PizzaSize, Int>,
    var revenue: Double
) {
    @Suppress("ReplaceWithEnumMap")
    constructor() : this(0, hashMapOf<PizzaType, Int>(), hashMapOf<PizzaSize, Int>(), 0.0)

    fun incrementNumOfOrders(pizzaType: PizzaType, pizzaSize: PizzaSize) {
        ordersCount++
        ordersPerPizza.merge(pizzaType, 1) { prev, curr -> prev + curr }
        ordersPerPizzaSize.merge(pizzaSize, 1) { prev, curr -> prev + curr }
    }

    fun addRevenue(price: Double) {
        revenue += price
    }
}
