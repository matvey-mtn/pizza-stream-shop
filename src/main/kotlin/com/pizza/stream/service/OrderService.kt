package com.pizza.stream.service

import com.pizza.stream.model.Pizza
import com.pizza.stream.model.PizzaSize

class OrderService {
    fun getPrice(pizza: Pizza): Double = when (pizza.size) {
        PizzaSize.SMALL -> pizza.pizzaType.price
        PizzaSize.MEDIUM -> pizza.pizzaType.price * 2
        PizzaSize.LARGE -> pizza.pizzaType.price * 3
    }
}
