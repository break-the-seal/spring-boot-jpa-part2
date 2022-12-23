package io.brick.jpabook.jpashop

import io.brick.jpabook.jpashop.domain.*
import io.brick.jpabook.jpashop.domain.item.Book
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

@Component
class InitDb(
    private val initService: InitService
) {
    // dbInit1() 작업을 직접 init()에 하면 Transactional이 걸리지 않게 된다.
    @PostConstruct
    fun init() {
        initService.dbInit1()
        initService.dbInit2()
    }
}

/**
 * 총 주문 2개
 * userA
 * - JPA1 BOOK
 * - JPA2 BOOK
 * userB
 * - SPRING1 BOOK
 * - SPRING2 BOOK
 */
@Component
@Transactional
class InitService(
    private val em: EntityManager,
) {
    fun dbInit1() {
        val member = createMember("userA", "서울", "1", "1111").also {
            em.persist(it)
        }

        val book1 = createBook("JPA1 BOOK", 10_000, 100).also {
            em.persist(it)
        }

        val book2 = createBook("JPA2 BOOK", 20_000, 100).also {
            em.persist(it)
        }

        val orderItem1 = OrderItem.createOrderItem(book1, 10_000, 1)
        val orderItem2 = OrderItem.createOrderItem(book2, 20_000, 2)

        val delivery = Delivery().apply {
            this.address = member.address
        }

        Order.createOrder(member, delivery, orderItem1, orderItem2).also {
            em.persist(it)
        }
    }

    fun dbInit2() {
        val member = createMember("userB", "판교", "2", "2222").also {
            em.persist(it)
        }

        val book1 = createBook("SPRING1 BOOK", 20_000, 200).also {
            em.persist(it)
        }

        val book2 = createBook("SPRING2 BOOK", 40_000, 300).also {
            em.persist(it)
        }

        val orderItem1 = OrderItem.createOrderItem(book1, 20_000, 3)
        val orderItem2 = OrderItem.createOrderItem(book2, 40_000, 2)

        val delivery = Delivery().apply {
            this.address = member.address
        }

        Order.createOrder(member, delivery, orderItem1, orderItem2).also {
            em.persist(it)
        }
    }

    private fun createMember(
        name: String,
        city: String,
        street: String,
        zipcode: String
    ): Member {
        return Member.createMember(
            name = name,
            address = Address(city, street, zipcode)
        )
    }

    private fun createBook(
        name: String,
        price: Int,
        stockQuantity: Int,
    ): Book {
        return Book().apply {
            this.name = name
            this.price = price
            this.stockQuantity = stockQuantity
        }
    }
}