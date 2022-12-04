package io.brick.jpabook.jpashop.domain.item

import io.brick.jpabook.jpashop.domain.Category
import io.brick.jpabook.jpashop.exception.NotEnoughStockException
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

    //==비즈니스 로직==//

    /**
     * stock 증가
     */
    fun addStock(quantity: Int) {
        this.stockQuantity += quantity
    }

    /**
     * stock 감소
     */
    fun removeStock(quantity: Int) {
        val restStock = this.stockQuantity - quantity
        if (restStock < 0) {
            throw NotEnoughStockException("need more stock")
        }

        this.stockQuantity = restStock
    }
}