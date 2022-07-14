package io.brick.jpabook.jpashop.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    val id: Long = 0L

    // 연관관계 메서드 적용 //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null
        set(member) {
            field = member!!
            member.orders.add(this)
        }

    // Cascade: persist(order) -> orderItem persist 같이 진행해준다.
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val orderItems: MutableList<OrderItem> = mutableListOf()

    // 연관관계 메서드 //
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "delivery_id")
    var delivery: Delivery? = null
        set(delivery) {
            field = delivery!!
            delivery.order = this
        }

    val orderDate: LocalDateTime? = null

    val status: OrderStatus? = null // 주문상태 [ORDER, CANCEL]

    // 연관관계 메서드 //
    fun addOrderItem(orderItem: OrderItem) {
        orderItems.add(orderItem)
        orderItem.order = this
    }
}
