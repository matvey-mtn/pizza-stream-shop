package com.pizza.stream.model

data class Order(
    val id: Int,
    val items: Pizza, // one pizza per order for simplicity
    val name: String,
    val totalPrice: Double
)
