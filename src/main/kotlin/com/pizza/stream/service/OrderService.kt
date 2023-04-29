package com.pizza.stream.service

import com.pizza.stream.model.Pizza
import com.pizza.stream.model.Price

class OrderService {
    fun getPrice(pizza: Pizza): Price? = BootstrapService.menuCache.prices[Pair(pizza.pizzaType, pizza.size)]
}
