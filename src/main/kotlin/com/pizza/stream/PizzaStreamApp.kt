package com.pizza.stream

import com.pizza.stream.Topics.ORDERS_TOPIC
import com.pizza.stream.Topics.PIZZA_SINK_TOPIC
import com.pizza.stream.service.OrderService
import java.time.Duration
import java.util.Properties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class PizzaStreamApp {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(PizzaStreamApp::class.java)
        private val ordersService = OrderService()
        private val ordersGenerator = OrdersGenerator(ordersService)

        @JvmStatic
        fun main(args: Array<String>) {

            val pizzaStreamTopologyFactory = PizzaStreamTopologyFactory(ORDERS_TOPIC, PIZZA_SINK_TOPIC)

            val pizzaStreamTopology = pizzaStreamTopologyFactory.createTopology()

            logger.info("Starting PizzaStream application...")
            logger.info("${pizzaStreamTopology.describe()}")

            val kafkaStreams = KafkaStreams(pizzaStreamTopology, streamProperties())
            setUncaughtExceptionHandler(kafkaStreams)
            addShutdownHook(kafkaStreams)

            kafkaStreams.start()
            startOrdersGenerator()
        }

        private fun startOrdersGenerator() {
            ordersGenerator.start()
        }

        private fun addShutdownHook(kafkaStreams: KafkaStreams) {
            Runtime.getRuntime().addShutdownHook(
                Thread(
                    {
                        closeKafkaStreamsAndOrdersGenerator(kafkaStreams)
                    }, "streams-close-thread"
                )
            )
        }

        private fun closeKafkaStreamsAndOrdersGenerator(kafkaStreams: KafkaStreams) {
            ordersGenerator.close()
            kafkaStreams.close(Duration.ofSeconds(1))
        }

        private fun setUncaughtExceptionHandler(kafkaStreams: KafkaStreams) {
            kafkaStreams.setUncaughtExceptionHandler { err ->
                logger.error("Shutting down PizzaStream App due to unexpected error", err)
                closeKafkaStreamsAndOrdersGenerator(kafkaStreams)
                StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION
            }
        }


        private fun streamProperties() = Properties().apply {
            put(StreamsConfig.APPLICATION_ID_CONFIG, "pizza-stream")
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
            put(StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE)
            put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 30 * 1000)
            put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4)
            put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 2)
            put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 50 * 1024 * 1024)
            put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
            put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass)

            /* Consumer Configs */
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
            put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500)
            put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000)
            put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30 * 1000)

            /* Producer Configs */
            put(ProducerConfig.ACKS_CONFIG, "1")
            put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip")
        }
    }
}
