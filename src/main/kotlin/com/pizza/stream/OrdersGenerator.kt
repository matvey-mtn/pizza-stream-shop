package com.pizza.stream

import com.pizza.stream.Topics.ORDERS_TOPIC
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
import kotlin.random.Random
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class OrdersGenerator(private val orderService: OrderService) : AutoCloseable {

    private val ordersCounter = AtomicInteger(1)
    private val orderId: Int
        get() = ordersCounter.getAndIncrement()

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
            ::orderRandomPizza,
            0,
            5,
            TimeUnit.SECONDS
        )
    }

    private fun orderRandomPizza() {
        val pizzaType = when(Random.nextInt(4)) {
            0 -> PizzaType.MARGHERITA
            1 -> PizzaType.PEPPERONI
            2 -> PizzaType.VEGGIE
            3 -> PizzaType.HAWAIIAN
            else -> PizzaType.MARGHERITA
        }

        val pizzaSize = when (Random.nextInt(3)) {
            0 -> PizzaSize.SMALL
            1 -> PizzaSize.MEDIUM
            2 -> PizzaSize.LARGE
            else -> PizzaSize.MEDIUM
        }

        val customerName = when (Random.nextInt(4)) {
            0 -> "Leonardo"
            1 -> "Michelangelo"
            2 -> "Donatello"
            3 -> "Raphael"
            else -> "Splitner"
        }

        val pizza = Pizza("${pizzaType.name.lowercase()}-$orderId", pizzaType, pizzaSize)

        val order = Order(
            orderId,
            pizza,
            customerName,
            orderService.getPrice(pizza)
        )

        kafkaProducer.send(ProducerRecord(ORDERS_TOPIC, order))
    }

    override fun close() {
        scheduledThreadPoolExecutor.shutdownNow()
    }
}
