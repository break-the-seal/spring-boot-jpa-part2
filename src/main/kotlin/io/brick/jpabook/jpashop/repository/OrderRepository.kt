package io.brick.jpabook.jpashop.repository

import io.brick.jpabook.jpashop.domain.Order
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
class OrderRepository(
    private val em: EntityManager
) {
    fun save(order: Order) {
        em.persist(order)
    }

    fun findOne(id: Long): Order? {
        return em.find(Order::class.java, id)
    }

//    fun findAll(orderSearch: OrderSearch): List<Order> {
//        return em.createQuery("select o from Order o", Order::class.java).resultList
//    }
}