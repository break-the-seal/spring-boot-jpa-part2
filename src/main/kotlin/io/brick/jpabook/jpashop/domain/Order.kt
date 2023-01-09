package io.brick.jpabook.jpashop.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "orders")
class Order protected constructor() {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    var id: Long = 0L

    // 연관관계 메서드 적용 //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null
        set(member) {
            field = member!!
            member.orders.add(this)
        }

    // Cascade: persist(order) -> orderItem persist 같이 진행해준다.
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orderItems: MutableList<OrderItem> = ArrayList()

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
        //== 생성 메서드 ==//
        fun createOrder(member: Member, delivery: Delivery, vararg orderItems: OrderItem): Order {
            return Order().apply {
                this.member = member
                this.delivery = delivery
                orderItems.forEach { this.addOrderItem(it) }

                this.status = OrderStatus.ORDER
                this.orderDate = LocalDateTime.now()
            }
        }
    }

    // 연관관계 메서드 //
    fun addOrderItem(orderItem: OrderItem) {
        orderItems.add(orderItem)
        orderItem.order = this
    }

    //== 비즈니스 로직 ==//
    /**
     * 주문 취소
     */
    fun cancel() {
        if (delivery!!.status == DeliveryStatus.COMP) {
            throw IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.")
        }

        this.status = OrderStatus.CANCEL
        orderItems.forEach { it.cancel() }
    }

    //== 조회 로직 ==//
    /**
     * 전체 주문 가격 조
     */
    fun getTotalPrice(): Int {
        return orderItems.sumOf { it.getTotalPrice() }
    }
}
