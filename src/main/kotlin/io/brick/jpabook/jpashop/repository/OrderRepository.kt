package io.brick.jpabook.jpashop.repository

import io.brick.jpabook.jpashop.domain.Order
import io.brick.jpabook.jpashop.domain.OrderStatus
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Predicate

@Repository
class OrderRepository(
    private val em: EntityManager,
) {
    fun save(order: Order) {
        em.persist(order)
    }

    fun findOne(id: Long): Order? {
        return em.find(Order::class.java, id)
    }

    fun findAllByString(orderSearch: OrderSearch): List<Order> {
        var jpql = "select o from Order o join o.member m"
        var isFirstCondition = true

        // 주문상태 검색
        orderSearch.orderStatus?.let {
            if (isFirstCondition) {
                jpql += " where"
                isFirstCondition = false
            } else {
                jpql += " and"
            }
            jpql += " o.status = :status"
        }

        if (StringUtils.hasText(orderSearch.memberName)) {
            if (isFirstCondition) {
                jpql += " where"
                isFirstCondition = false
            } else {
                jpql += " and"
            }
            jpql += " m.name like :name"
        }

        var query = em.createQuery(jpql, Order::class.java)
            .setMaxResults(1_000) // 최대 1_000 건

        orderSearch.orderStatus?.let {
            query = query.setParameter("status", orderSearch.orderStatus)
        }

        if (StringUtils.hasText(orderSearch.memberName)) {
            query = query.setParameter("name", orderSearch.memberName)
        }

        return query.resultList
    }

    /**
     * JPA Criteria
     */
    fun findAllByCriteria(orderSearch: OrderSearch): List<Order> {
        val cb: CriteriaBuilder = em.criteriaBuilder
        val cq = cb.createQuery(Order::class.java)
        val o = cq.from(Order::class.java)
        val m = o.join<Any, Any>("member", JoinType.INNER)

        val criteria: MutableList<Predicate> = ArrayList()

        // 주문 상태 검색
        orderSearch.orderStatus?.run {
            val status = cb.equal(o.get<OrderStatus>("status"), orderSearch.orderStatus)
            criteria.add(status)
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.memberName)) {
            val name = cb.like(m.get("name"), "%${orderSearch.memberName}%")
            criteria.add(name)
        }

        cq.where(cb.and(*criteria.toTypedArray()))
        val query = em.createQuery(cq).setMaxResults(1_000)

        return query.resultList
    }

    fun findAllWithMemberDelivery(): List<Order> {
        // fetch join 이용 -> LAZY proxy 설정되어 있어도 proxy 없이 진짜 Entity를 가져옴
        return em.createQuery(
            """
                |SELECT o FROM Order o
                | join fetch o.member m
                | join fetch o.delivery d
            """.trimMargin(),
            Order::class.java
        ).resultList
    }

    fun findAllWithItem(): List<Order> {
        return em.createQuery(
            """
                |select distinct o from Order o
                |    join fetch o.member m
                |    join fetch o.delivery d
                |    join fetch o.orderItems oi
                |    join fetch oi.item i
            """.trimMargin(),
            Order::class.java
        ).resultList
    }

    fun findAllWithMemberDelivery(offset: Int, limit: Int): List<Order> {
        return em.createQuery(
            """
                |SELECT o FROM Order o
                | join fetch o.member m
                | join fetch o.delivery d
            """.trimMargin(),
            Order::class.java
        )
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }
}