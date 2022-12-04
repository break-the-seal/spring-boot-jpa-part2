package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.Address
import io.brick.jpabook.jpashop.domain.Member
import io.brick.jpabook.jpashop.domain.OrderStatus
import io.brick.jpabook.jpashop.domain.item.Book
import io.brick.jpabook.jpashop.exception.NotEnoughStockException
import io.brick.jpabook.jpashop.repository.OrderRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
internal class OrderServiceTest {
    @Autowired
    private lateinit var em: EntityManager
    @Autowired
    private lateinit var orderService: OrderService
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Test
    fun 상품주문() {
        // given
        val member = createMember()
        val book = createBook("시골 JPA", 10_000, 10)

        val orderCount: Int = 2

        // when
        val orderId = orderService.order(
            memberId = member.id,
            itemId = book.id,
            count = orderCount
        )

        // then
        val findOrder = orderRepository.findOne(orderId)

        assertEquals(OrderStatus.ORDER, findOrder!!.status, "상품 주문시 상태는 ORDER")
        assertEquals(1, findOrder.orderItems.size, "주문한 상품 종류 수가 정확해야 한다.")
        assertEquals(10_000 * orderCount, findOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.")
        assertEquals(8, book.stockQuantity , "주문 수량만큼 재고가 줄어야 한다.")
    }



    @Test
    fun 주문취소() {
        // given
        val member = createMember()
        val book = createBook("시골 JPA", 10_000, 10)

        val orderCount: Int = 2

        val orderId = orderService.order(
            memberId = member.id,
            itemId = book.id,
            count = orderCount
        )

        // when
        orderService.cancelOrder(orderId)

        // then
        val findOrder = orderRepository.findOne(orderId)

        assertEquals(OrderStatus.CANCEL, findOrder!!.status, "주문 취소시 상태는 CANCEL 이어야 한다.")
        assertEquals(10, book.stockQuantity, "주문 취소된 상품은 그만큼 재고가 증가해야 한다.")
    }

    @Test
    fun 상품주문_재고수량초과() {
        // given
        val member = createMember()
        val book = createBook("시골 JPA", 10_000, 10)

        val orderCount: Int = 11 // 재고수량보다 많은 주문수량

        assertThrows<NotEnoughStockException>("재고 수량 부족 예외가 발생해야 한다.") {
            // when
            orderService.order(
                memberId = member.id,
                itemId = book.id,
                count = orderCount
            )
        }
    }

    private fun createBook(name: String, price: Int, stockQuantity: Int): Book {
        val book = Book().apply {
            this.name = name
            this.price = price
            this.stockQuantity = stockQuantity
        }
        em.persist(book)
        return book
    }

    private fun createMember(): Member {
        val member = Member().apply {
            name = "회원1"
            address = Address("서울", "강가", "123-123")
        }
        em.persist(member)
        return member
    }
}