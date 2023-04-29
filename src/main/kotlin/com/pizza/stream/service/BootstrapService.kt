package com.pizza.stream.service

import com.pizza.stream.model.Ingredient
import com.pizza.stream.model.Menu
import com.pizza.stream.model.PizzaRecipe
import com.pizza.stream.model.PizzaSize
import com.pizza.stream.model.PizzaType

object BootstrapService {
    val recipesCache: Map<PizzaType, PizzaRecipe> = hashMapOf(
        PizzaType.MARGHERITA to PizzaRecipe(
            ingredients = mapOf(
                Ingredient("dough") to 1.0,
                Ingredient("mozzarella cheese") to 2.0,
                Ingredient("tomatoes") to 0.5,
                Ingredient("basil") to 0.2,
                Ingredient("olive oil") to 0.1,

            )
        ),

        PizzaType.PEPPERONI to PizzaRecipe(ingredients = mapOf(
            Ingredient("dough") to 1.0,
            Ingredient("mozzarella cheese") to 0.5,
            Ingredient("olives") to 0.5,
            Ingredient("bell peppers") to 0.5,
            Ingredient("sausage") to 0.5
        )),

        PizzaType.HAWAIIAN to PizzaRecipe(ingredients = mapOf(
            Ingredient("dough") to 1.0,
            Ingredient("mozzarella cheese") to 0.5,
            Ingredient("pineapple chunks") to 1.0,
            Ingredient("ham") to 0.5,
            Ingredient("onions") to 0.5
        )),

        PizzaType.VEGGIE to PizzaRecipe(ingredients = mapOf(
            Ingredient("dough") to 1.0,
            Ingredient("bell peppers") to 1.0,
            Ingredient("tomato") to 2.0,
            Ingredient("olives") to 1.0
        )),
    )

    val menuCache = Menu(prices = mapOf(
        Pair(PizzaType.MARGHERITA, PizzaSize.SMALL) to 4.99,
        Pair(PizzaType.MARGHERITA, PizzaSize.MEDIUM) to 8.99,
        Pair(PizzaType.MARGHERITA, PizzaSize.LARGE) to 11.99,
        Pair(PizzaType.PEPPERONI, PizzaSize.SMALL) to 5.99,
        Pair(PizzaType.PEPPERONI, PizzaSize.MEDIUM) to 9.99,
        Pair(PizzaType.PEPPERONI, PizzaSize.LARGE) to 12.99,
        Pair(PizzaType.HAWAIIAN, PizzaSize.SMALL) to 6.50,
        Pair(PizzaType.HAWAIIAN, PizzaSize.MEDIUM) to 10.00,
        Pair(PizzaType.HAWAIIAN, PizzaSize.LARGE) to 14.99,
        Pair(PizzaType.VEGGIE, PizzaSize.SMALL) to 4.49,
        Pair(PizzaType.VEGGIE, PizzaSize.MEDIUM) to 7.49,
        Pair(PizzaType.VEGGIE, PizzaSize.LARGE) to 9.99,
    ))
}
