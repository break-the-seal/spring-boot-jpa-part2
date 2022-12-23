package io.brick.jpabook.jpashop.domain.api

import io.brick.jpabook.jpashop.domain.Order
import io.brick.jpabook.jpashop.repository.OrderRepository
import io.brick.jpabook.jpashop.repository.OrderSearch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
class OrderSimpleApiController(
    private val orderRepository: OrderRepository
) {
    @GetMapping("/api/v1/simple-orders")
    fun ordersV1(): List<Order> {
        val orders = orderRepository.findAllByString(OrderSearch())
        orders.forEach {
            it.member?.name // lazy 강제 초기화
            it.delivery?.address // lazy 강제 초기화
        }

        return orders
    }
}