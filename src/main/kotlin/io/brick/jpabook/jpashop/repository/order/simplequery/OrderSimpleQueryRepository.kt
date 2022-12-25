package io.brick.jpabook.jpashop.repository.order.simplequery

import io.brick.jpabook.jpashop.repository.OrderSimpleQueryDto
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
class OrderSimpleQueryRepository(
    private val em: EntityManager
) {
    fun findOrderDtos(): List<OrderSimpleQueryDto> {
        // JPQL을 이용해서 Dto 형태로 반환받기
        // 이렇게 되면 실제 select projection 지정한 칼럼만 select 됨
        // (fetch join은 전체 컬럼 select)
        return em.createQuery(
            """
                |SELECT new io.brick.jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) 
                |FROM Order o
                | join o.member m
                | join o.delivery d
            """.trimMargin(),
            OrderSimpleQueryDto::class.java
        ).resultList
    }
}