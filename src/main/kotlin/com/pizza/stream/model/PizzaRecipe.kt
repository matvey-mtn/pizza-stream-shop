package com.pizza.stream.model

typealias Amount = Double

data class PizzaRecipe(
    val ingredients: Map<Ingredient, Amount>
)
