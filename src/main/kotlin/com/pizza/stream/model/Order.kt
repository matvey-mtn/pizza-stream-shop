package com.pizza.stream.model

data class Order(
    val id: Int,
    val items: List<Pizza>,
    val customerFirstName: String,
    val customerLastName: String?,
    val employerId: String,
    val totalPrice: Double
)
