package io.brick.jpabook.jpashop.repository.order.query

import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
class OrderQueryRepository(
    private val em: EntityManager,
) {
    fun findOrderQueryDtos(): List<OrderQueryDto> {
        val orders = findOrders().onEach {
            it.apply {
                val orderItems = findOrderItems(this.orderId)
                this.orderItems = orderItems
            }
        }

        return orders
    }

    private fun findOrders(): List<OrderQueryDto> =
        em.createQuery(
            """
                |select new io.brick.jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) 
                |from Order o
                | join o.member m
                | join o.delivery d
            """.trimMargin(),
            OrderQueryDto::class.java
        )
            .resultList

    private fun findOrderItems(orderId: Long): List<OrderItemQueryDto> {
        return em.createQuery(
            """
                |select new io.brick.jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)
                |from OrderItem oi
                | join oi.item i
                |where oi.order.id = :orderId
            """.trimMargin(),
            OrderItemQueryDto::class.java
        )
            .setParameter("orderId", orderId)
            .resultList
    }

}