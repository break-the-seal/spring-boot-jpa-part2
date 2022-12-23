package io.brick.jpabook.jpashop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.brick.jpabook.jpashop.domain.item.Item
import javax.persistence.*

@Entity
class OrderItem protected constructor() {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    var id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var item: Item? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null

    var orderPrice: Int = 0 // 주문 가격

    var count: Int = 0 // 주문 수량

    companion object {
        //== 생성 메서드 ==//
        fun createOrderItem(item: Item, orderPrice: Int, count: Int): OrderItem {
            return OrderItem().apply {
                this.item = item
                this.orderPrice = orderPrice
                this.count = count

                item.removeStock(count)
            }
        }
    }

    //== 비즈니스 로직 ==//
    fun cancel() {
        item!!.addStock(count)
    }

    //== 조회 로직 ==//
    fun getTotalPrice(): Int {
        return orderPrice * count
    }
}
