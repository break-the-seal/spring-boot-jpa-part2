package io.brick.jpabook.jpashop.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue
    @Column(name = "order_id")
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,


    @OneToMany(mappedBy = "order")
    val orderItems: MutableList<OrderItem> = mutableListOf(),

    @OneToOne
    @JoinColumn(name = "delivery_id")
    val delivery: Delivery,

    val orderDate: LocalDateTime,

    val status: OrderStatus // 주문상태 [ORDER, CANCEL]
)
