package com.pizza.stream.serde

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pizza.stream.model.Order
import com.pizza.stream.model.OrderProcessingDetails
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

object PizzaSerdes {

    val objMapper = jacksonObjectMapper()

    @Suppress("FunctionName")
    @JvmStatic
    fun OrdersSerde() = OrdersSerdeImpl()

    @Suppress("FunctionName")
    @JvmStatic
    fun OrderProcessingDetailsSerde() = OrderProcessingDetailsSerdeImpl()

    @Suppress("FunctionName")
    @JvmStatic
    fun OrdersSerializer() = OrdersSerializerImpl()

    class OrdersSerdeImpl : Serde<Order> {
        override fun serializer(): Serializer<Order> {
            return Serializer<Order> { topic, data ->
                data?.let { objMapper.writeValueAsBytes(it) } ?: byteArrayOf()
            }
        }

        override fun deserializer(): Deserializer<Order> {
            return Deserializer<Order> { topic, data ->
                objMapper.readValue(data, Order::class.java)
            }
        }
    }

    class OrderProcessingDetailsSerdeImpl : Serde<OrderProcessingDetails> {
        override fun serializer(): Serializer<OrderProcessingDetails> {
            return Serializer<OrderProcessingDetails> { topic, data ->
                data?.let { objMapper.writeValueAsBytes(it) } ?: byteArrayOf()
            }
        }

        override fun deserializer(): Deserializer<OrderProcessingDetails> {
            return Deserializer<OrderProcessingDetails> { topic, data ->
                objMapper.readValue(data, OrderProcessingDetails::class.java)
            }
        }
    }

    class OrdersSerializerImpl : Serializer<Order> {
        override fun serialize(topic: String?, data: Order?): ByteArray {
            return data?.let { objMapper.writeValueAsBytes(it) } ?: byteArrayOf()
        }

    }
}
