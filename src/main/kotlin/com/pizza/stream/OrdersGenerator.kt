package com.pizza.stream

import com.pizza.stream.model.Order
import com.pizza.stream.model.Pizza
import com.pizza.stream.model.PizzaSize
import com.pizza.stream.model.PizzaType
import com.pizza.stream.serde.PizzaSerdes
import com.pizza.stream.service.OrderService
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class OrdersGenerator(private val orderService: OrderService) : AutoCloseable {

    private val generatedId = AtomicInteger(1)
    private val kafkaProducer = KafkaProducer<String, Order>(
        Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
            put(ProducerConfig.ACKS_CONFIG, "1")
            put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip")
            put(ProducerConfig.LINGER_MS_CONFIG, 200)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PizzaSerdes.OrdersSerializer()::class.java)
        }
    )

    private val scheduledThreadPoolExecutor = Executors.newSingleThreadScheduledExecutor()

    fun start() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
            {
                val orderId = generatedId.getAndIncrement()
                val pizza = Pizza("Margheritta-$orderId", PizzaType.MARGHERITA, PizzaSize.MEDIUM)
                val order = Order(
                    orderId,
                    listOf(pizza),
                    "Matt",
                    null,
                    "101",
                    orderService.getPrice(pizza)!!
                )
                kafkaProducer.send(ProducerRecord("orders", order))
            },
            0,
            5,
            TimeUnit.SECONDS
        )
    }

    override fun close() {
        scheduledThreadPoolExecutor.shutdownNow()
    }
}
