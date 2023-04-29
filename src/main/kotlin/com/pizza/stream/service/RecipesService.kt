package com.pizza.stream.service

import com.pizza.stream.model.Pizza
import com.pizza.stream.model.PizzaRecipe

class RecipesService {
    fun getRecipe(pizza: Pizza): PizzaRecipe? {
        return BootstrapService.recipesCache[pizza.pizzaType]
    }
}
