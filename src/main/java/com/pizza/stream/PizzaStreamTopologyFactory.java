package com.pizza.stream;

import com.pizza.stream.model.Order;
import com.pizza.stream.model.OrderProcessingDetails;
import com.pizza.stream.model.Pizza;
import com.pizza.stream.model.Recipe;
import com.pizza.stream.model.Status;
import com.pizza.stream.serde.PizzaSerdes;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PizzaStreamTopologyFactory {

    public final String incomingOrdersTopicName;
    public final String pizzaDeliveryTopicName;

    private final Logger logger = LoggerFactory.getLogger(PizzaStreamTopologyFactory.class);

    public PizzaStreamTopologyFactory(String incomingOrdersTopicName, String pizzaDeliveryTopicName) {
        this.incomingOrdersTopicName = incomingOrdersTopicName;
        this.pizzaDeliveryTopicName = pizzaDeliveryTopicName;
    }

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Order> stream = builder.stream(
            incomingOrdersTopicName,
            Consumed.with(Serdes.String(), PizzaSerdes.OrdersSerde())
        );

        stream
            .mapValues(this::assembleOrder, Named.as("AssembleProcessor"))
            .mapValues(this::putIntoOven, Named.as("BackingProcessor"))
            .mapValues(this::packagePizza, Named.as("PackagingProcessor"))
            .mapValues(this::completeOrderProcessing, Named.as("MarkOrderAsCompleted"))
            .to(pizzaDeliveryTopicName, Produced.valueSerde(PizzaSerdes.OrderProcessingDetailsSerde()));

        return builder.build();
    }

    @NotNull
    private OrderProcessingDetails assembleOrder(Order order) {
        var pizza = order.getItems();
        var recipe = pizza.getPizzaType().getRecipe();

        Map<String, Integer> ingredients = ingredientsAdjustedToSize(pizza, recipe);

        logger.info("ASSEMBLING [orderId {}]", order.getId());
        logger.info("Getting required ingredients for {} pizza {}: [{}]",
            order.getItems().getSize(),
            order.getItems().getName(),
            ingredients
        );

        try {
            // Simulate assembling ingredients for pizza
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new OrderProcessingDetails(order, pizza, ingredients, Status.ASSEMBLED);
    }


    @NotNull
    private OrderProcessingDetails putIntoOven(OrderProcessingDetails opd) {
        logger.info("BACKING pizza {} [orderId {}]", opd.getPizza().getPizzaType().name(), opd.getOrder().getId());
        try {
            Thread.sleep(5000); // simulate backing in the oven
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new OrderProcessingDetails(
            opd.getOrder(), opd.getPizza(),
            opd.getIngredients(), Status.BACKED);
    }

    @NotNull
    private OrderProcessingDetails packagePizza(OrderProcessingDetails orderDetails) {
        logger.info("PACKAGING pizza {} [orderId {}]",
            orderDetails.getPizza().getPizzaType().name(), orderDetails.getOrder().getId()
        );

        try {
            Thread.sleep(1000); // simulating packaging
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Pizza pizza = orderDetails.getPizza();
        pizza.setReady(true);

        return new OrderProcessingDetails(
            orderDetails.getOrder(), pizza,
            orderDetails.getIngredients(), Status.PACKAGED
        );
    }

    private OrderProcessingDetails completeOrderProcessing(OrderProcessingDetails orderDetails) {
        logger.info("ORDER COMPLETED [orderId {}]", orderDetails.getOrder().getId());
        orderDetails.setStatus(Status.COMPLETED);
        return orderDetails;
    }

    @NotNull
    @SuppressWarnings("CodeBlock2Expr")
    private static Map<String, Integer> ingredientsAdjustedToSize(Pizza pizza, Recipe recipe) {
        return recipe.getIngredients().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    return switch (pizza.getSize()) {
                        case SMALL -> entry.getValue();
                        case MEDIUM -> entry.getValue() * 2;
                        case LARGE -> entry.getValue() * 3;
                    };
                }
            ));
    }
}
