package io.brick.jpabook.jpashop.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    var id: Long? = null

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
    var orderItems: MutableList<OrderItem> = mutableListOf()

    // 연관관계 메서드 //
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "delivery_id")
    var delivery: Delivery? = null
        set(delivery) {
            field = delivery!!
            delivery.order = this
        }

    var orderDate: LocalDateTime? = null

    var status: OrderStatus? = null // 주문상태 [ORDER, CANCEL]

    companion object {
        fun createOrder(member: Member, delivery: Delivery, vararg orderItems: OrderItem) {
            val order = Order().apply {
                this.member = member
                this.delivery = delivery
                orderItems.forEach { this.addOrderItem(it) }

                this.status = OrderStatus.ORDER

            }


        }
    }

    // 연관관계 메서드 //
    fun addOrderItem(orderItem: OrderItem) {
        orderItems.add(orderItem)
        orderItem.order = this
    }
}
