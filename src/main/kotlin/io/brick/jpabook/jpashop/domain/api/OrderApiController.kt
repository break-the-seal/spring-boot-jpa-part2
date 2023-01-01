package io.brick.jpabook.jpashop.domain.api

import io.brick.jpabook.jpashop.domain.Address
import io.brick.jpabook.jpashop.domain.Order
import io.brick.jpabook.jpashop.domain.OrderItem
import io.brick.jpabook.jpashop.domain.OrderStatus
import io.brick.jpabook.jpashop.repository.OrderRepository
import io.brick.jpabook.jpashop.repository.OrderSearch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class OrderApiController(
    private val orderRepository: OrderRepository,
) {
    @GetMapping("/api/v1/orders")
    fun orderV1(): List<Order> {
        val orders = orderRepository.findAllByString(OrderSearch())
        // LAZY proxy 초기화
        orders.forEach {
            it.member?.name
            it.delivery?.address
            it.orderItems.forEach { o ->
                o.item?.name
            }
        }

        return orders
    }

    @GetMapping("/api/v2/orders")
    fun orderV2(): List<OrderDto> {
        val orders = orderRepository.findAllByString(OrderSearch())
        // LAZY proxy 초기화
        return orders.map {
            OrderDto.of(it)
        }
    }
}

data class OrderDto(
    val orderId: Long,
    val name: String,
    val orderDate: LocalDateTime,
    val orderStatus: OrderStatus,
    val address: Address,
    val orderItems: List<OrderItemDto>,
) {
    companion object {
        fun of(order: Order): OrderDto {
            return OrderDto(
                orderId = order.id,
                name = order.member!!.name,
                orderDate = order.orderDate!!,
                orderStatus = order.status!!,
                address = order.delivery!!.address!!,
                // orderItems > item 초기화
                orderItems = order.orderItems.map {
                    OrderItemDto.of(it)
                }
            )
        }
    }
}

data class OrderItemDto(
    val itemName: String, // 상품명
    val orderPrice: Int, // 주문 가격
    val count: Int, // 주문 수량
) {
    companion object {
        fun of(orderItem: OrderItem): OrderItemDto {
            return OrderItemDto(
                itemName = orderItem.item!!.name,
                orderPrice = orderItem.orderPrice,
                count = orderItem.count
            )
        }
    }
}