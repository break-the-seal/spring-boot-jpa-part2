package io.brick.jpabook.jpashop.domain.item

import io.brick.jpabook.jpashop.domain.Category
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    var id: Long = 0L

    var name: String = ""

    var price: Int = 0

    var stockQuantity: Int = 0

    @ManyToMany(mappedBy = "items")
    var categories: MutableList<Category> = mutableListOf()
}