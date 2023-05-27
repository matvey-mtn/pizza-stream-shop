package com.pizza.stream.model

typealias AmountInGrams = Int
typealias Name = String

data class Recipe(
    val ingredients: Map<Name, AmountInGrams>
)
