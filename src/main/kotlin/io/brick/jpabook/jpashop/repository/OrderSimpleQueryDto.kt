package io.brick.jpabook.jpashop.repository

import io.brick.jpabook.jpashop.domain.Address
import io.brick.jpabook.jpashop.domain.Order
import io.brick.jpabook.jpashop.domain.OrderStatus
import java.time.LocalDateTime

data class OrderSimpleQueryDto(
    val orderId: Long,
    val name: String,
    val orderDate: LocalDateTime,
    val orderStatus: OrderStatus,
    val address: Address
) {
    companion object {
        fun of(order: Order): OrderSimpleQueryDto {
            return OrderSimpleQueryDto(
                order.id,
                order.member!!.name,
                order.orderDate!!,
                order.status!!,
                order.delivery!!.address!!
            )
        }
    }
}