package com.pizza.stream;

import com.pizza.stream.model.OrderProcessingDetails;
import com.pizza.stream.model.aggregations.Analytics;
import com.pizza.stream.serde.PizzaSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

public class AnalyticsTopologyFactory {
    public final String pizzaSinkTopic;
    public final String analyticsTopic;

    public AnalyticsTopologyFactory(String pizzaSinkTopic, String analyticsTopic) {
        this.pizzaSinkTopic = pizzaSinkTopic;
        this.analyticsTopic = analyticsTopic;
    }

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, OrderProcessingDetails> analyticsStream = builder.stream(
            pizzaSinkTopic,
            Consumed.with(Serdes.String(), PizzaSerdes.OrderProcessingDetailsSerde())
        );

        analyticsStream
            .groupBy((key, value) -> "foo")
            .aggregate(
                Analytics::new,
                (key, orderDetails, aggregate) -> {
                    aggregate.incrementNumOfOrders(
                        orderDetails.getPizza().getPizzaType(),
                        orderDetails.getPizza().getSize()
                    );
                    aggregate.addRevenue(orderDetails.getOrder().getTotalPrice());
                    return aggregate;
                },
                Materialized.<String, Analytics, KeyValueStore<Bytes, byte[]>>as("AnalyticsStore")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(PizzaSerdes.AnalyticsSerde())
            )
            .toStream()
            .to(analyticsTopic, Produced.valueSerde(PizzaSerdes.AnalyticsSerde()));

        return builder.build();
    }

}
