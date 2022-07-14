package io.brick.jpabook.jpashop.domain

import javax.persistence.*

@Entity
class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    val id: Long = 0L

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    var order: Order? = null

    @Embedded
    var address: Address? = null

    @Enumerated(EnumType.STRING)
    var status: DeliveryStatus? = null // READY, COMP
}
