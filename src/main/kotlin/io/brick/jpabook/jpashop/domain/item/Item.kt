package io.brick.jpabook.jpashop.domain.item

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    val id: Long = 0L

    val name: String = ""

    val price: Int = 0

    val stockQuantity: Int = 0
}