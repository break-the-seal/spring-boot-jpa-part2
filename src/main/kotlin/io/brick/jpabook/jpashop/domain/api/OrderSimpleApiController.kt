package io.brick.jpabook.jpashop.domain.api

import io.brick.jpabook.jpashop.domain.Order
import io.brick.jpabook.jpashop.repository.OrderRepository
import io.brick.jpabook.jpashop.repository.OrderSearch
import io.brick.jpabook.jpashop.repository.OrderSimpleQueryDto
import io.brick.jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository
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
    private val orderRepository: OrderRepository,
    private val orderSimpleQueryRepository: OrderSimpleQueryRepository
) {
    /**
     * Entity 그대로 반환
     * (Entity 반환은 절대 하지 말 것)
     * @return List<Order>
     */
    @GetMapping("/api/v1/simple-orders")
    fun ordersV1(): List<Order> {
        val orders = orderRepository.findAllByString(OrderSearch())
        orders.forEach {
            it.member?.name // lazy 강제 초기화
            it.delivery?.address // lazy 강제 초기화
        }

        return orders
    }

    /**
     * DTO로 변환해서 return
     * (N+1 문제 발생)
     * @return List<SimpleOrderDto>
     */
    @GetMapping("/api/v2/simple-orders")
    fun ordersV2(): List<OrderSimpleQueryDto> {
        return orderRepository.findAllByString(OrderSearch()).map {
            OrderSimpleQueryDto.of(it)
        }
    }

    /**
     * Fetch Join 이용한 쿼리 조회 (쿼리 한 번 나감)
     * @return List<SimpleOrderDto>
     */
    @GetMapping("/api/v3/simple-orders")
    fun ordersV3(): List<OrderSimpleQueryDto> {
        return orderRepository.findAllWithMemberDelivery().map {
            OrderSimpleQueryDto.of(it)
        }
    }

    @GetMapping("/api/v4/simple-orders")
    fun ordersV4(): List<OrderSimpleQueryDto> {
        return orderSimpleQueryRepository.findOrderDtos()
    }
}