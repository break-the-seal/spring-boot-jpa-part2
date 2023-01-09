package io.brick.jpabook.jpashop.repository.order.query

import io.brick.jpabook.jpashop.domain.Address
import io.brick.jpabook.jpashop.domain.OrderStatus
import java.time.LocalDateTime

data class OrderQueryDto(
    val orderId: Long,
    val name: String,
    val orderDate: LocalDateTime,
    val orderStatus: OrderStatus,
    val address: Address,
) {
    var orderItems: List<OrderItemQueryDto> = emptyList()
}