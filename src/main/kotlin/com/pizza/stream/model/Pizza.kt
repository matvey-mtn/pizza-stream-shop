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

enum class PizzaType(val recipe: Recipe, val price: Double) {
    MARGHERITA(
        recipe = Recipe(
            mapOf(
                "dough" to 300,
                "tomato paste" to 100,
                "mozzarella cheese" to 150,
                "basil" to 50,
                "olive oil" to 30,
            )
        ),
        price = 4.99
    ),
    PEPPERONI(
        recipe = Recipe(
            mapOf(
                "dough" to 300,
                "tomato paste" to 100,
                "mozzarella cheese" to 150,
                "olives" to 50,
                "bell peppers" to 100,
                "sausage" to 100
            )
        ),
        price = 5.99
    ),
    VEGGIE(
        recipe = Recipe(
            mapOf(
                "dough" to 300,
                "tomato paste" to 100,
                "veggie cheese" to 150,
                "bell peppers" to 100,
                "olives" to 50
            )
        ),
        price = 4.49
    ),
    HAWAIIAN(
        recipe = Recipe(
            mapOf(
                "dough" to 300,
                "tomato paste" to 100,
                "mozzarella cheese" to 150,
                "pineapple chunks" to 100,
                "ham" to 100,
                "onion" to 50
            )
        ),
        price = 6.50
    )
}
