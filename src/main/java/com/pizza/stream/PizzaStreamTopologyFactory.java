package com.pizza.stream;

import com.pizza.stream.model.Order;
import com.pizza.stream.model.OrderProcessingDetails;
import com.pizza.stream.model.Pizza;
import com.pizza.stream.model.PizzaRecipe;
import com.pizza.stream.model.Status;
import com.pizza.stream.serde.PizzaSerdes;
import com.pizza.stream.service.RecipesService;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    public final RecipesService recipesService;

    private final Logger logger = LoggerFactory.getLogger(PizzaStreamTopologyFactory.class);

    public PizzaStreamTopologyFactory(String incomingOrdersTopicName, String pizzaDeliveryTopicName,
                                      RecipesService recipesService) {
        this.incomingOrdersTopicName = incomingOrdersTopicName;
        this.pizzaDeliveryTopicName = pizzaDeliveryTopicName;
        this.recipesService = recipesService;
    }

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Order> stream = builder.stream(incomingOrdersTopicName,
            Consumed.with(Serdes.String(), PizzaSerdes.OrdersSerde())
        );

        stream
            .filter((key, value) -> !value.getItems().isEmpty())
            .mapValues(this::assembleOrder, Named.as("AssembleProcessor"))
            .flatMapValues((key, value) -> value)
            .mapValues(this::putIntoOven, Named.as("BackingProcessor"))
            .mapValues(this::putBakedPizzaInTheBox, Named.as("PackagingProcessor"))
            .mapValues(this::completeOrderProcessing, Named.as("MarkOrderAsCompleted"))
            .to(pizzaDeliveryTopicName, Produced.valueSerde(PizzaSerdes.OrderProcessingDetailsSerde()));

        return builder.build();
    }


    private OrderProcessingDetails completeOrderProcessing(OrderProcessingDetails opd) {
        logger.info("Order {} completed", opd.getOrder().getId());
        opd.setStatus(Status.COMPLETED);
        return opd;
    }

    @NotNull
    private List<OrderProcessingDetails> assembleOrder(Order order) {
        return order.getItems().stream()
            .map(pizza -> {
                PizzaRecipe recipe = recipesService.getRecipe(pizza);
                if (recipe == null) return null;

                var ingredientsAdjustedToSize = new HashMap<>(recipe.getIngredients());

                switch (pizza.getSize()) {
                    case MEDIUM -> ingredientsAdjustedToSize.replaceAll((key, value) -> value * 2);
                    case LARGE -> ingredientsAdjustedToSize.replaceAll((key, value) -> value * 3);
                    default -> {
                    }
                }

                return new OrderProcessingDetails(order, pizza, ingredientsAdjustedToSize, Status.ASSEMBLED);
            })
            .filter(Objects::nonNull)
            .peek(orderDetails -> {
                logger.info("Assembling orderId {}", order.getId());
                logger.info("Getting required ingredients {} for pizza {}",
                    orderDetails.getIngredientsAdjustedToSize(),
                    orderDetails.getPizza().getPizzaType().name()
                );

                try {
                    // Simulate assembling ingredients for pizza
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();
    }

    @NotNull
    private OrderProcessingDetails putIntoOven(OrderProcessingDetails opd) {
        logger.info("Baking pizza {} from orderId {}", opd.getPizza().getPizzaType().name(), opd.getOrder().getId());
        try {
            Thread.sleep(5000); // simulate backing in the oven
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new OrderProcessingDetails(
            opd.getOrder(), opd.getPizza(),
            opd.getIngredientsAdjustedToSize(), Status.BACKED);
    }

    @NotNull
    private OrderProcessingDetails putBakedPizzaInTheBox(OrderProcessingDetails opd) {
        logger.info("Packaging pizza {} from orderId {}", opd.getPizza().getPizzaType().name(), opd.getOrder().getId());
        try {
            Thread.sleep(1000); // simulating packaging
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Pizza pizza = opd.getPizza();
        pizza.setReady(true);

        return new OrderProcessingDetails(
            opd.getOrder(), pizza,
            opd.getIngredientsAdjustedToSize(), Status.PACKAGED
        );
    }
}
