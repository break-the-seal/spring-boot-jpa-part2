package io.brick.jpabook.jpashop.domain

import io.brick.jpabook.jpashop.domain.item.Item
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class OrderItem(
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "item_id")
    val item: Item,

    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: Order,

    val orderPrice: Int, // 주문 가격

    val count: Int // 주문 수량
)
