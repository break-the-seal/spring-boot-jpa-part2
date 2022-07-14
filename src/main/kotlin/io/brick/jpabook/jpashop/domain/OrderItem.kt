package io.brick.jpabook.jpashop.domain

import io.brick.jpabook.jpashop.domain.item.Item
import javax.persistence.*

@Entity
class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    var id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var item: Item? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null

    var orderPrice: Int = 0 // 주문 가격

    var count: Int = 0 // 주문 수량
}
