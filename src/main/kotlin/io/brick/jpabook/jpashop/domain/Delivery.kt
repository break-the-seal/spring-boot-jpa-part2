package io.brick.jpabook.jpashop.domain

import javax.persistence.*

@Entity
data class Delivery(
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    val id: Long = 0L,

    @OneToOne(mappedBy = "delivery")
    val order: Order,

    @Embedded
    val address: Address,

    @Enumerated(EnumType.STRING)
    val status: DeliveryStatus // READY, COMP
)
