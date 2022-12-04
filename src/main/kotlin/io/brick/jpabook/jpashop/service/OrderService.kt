package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.Delivery
import io.brick.jpabook.jpashop.domain.Order
import io.brick.jpabook.jpashop.domain.OrderItem
import io.brick.jpabook.jpashop.exception.NotFoundException
import io.brick.jpabook.jpashop.repository.ItemRepository
import io.brick.jpabook.jpashop.repository.MemberRepository
import io.brick.jpabook.jpashop.repository.OrderRepository
import io.brick.jpabook.jpashop.repository.OrderSearch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val itemRepository: ItemRepository,
) {

    /**
     * 주문
     */
    @Transactional
    fun order(memberId: Long, itemId: Long, count: Int): Long {
        // 맴버 엔티티 조회
        val member = memberRepository.findOne(memberId)
            ?: throw NotFoundException("존재하지 않는 회원입니다. ${memberId}")
        val item = itemRepository.findOne(itemId)
            ?: throw NotFoundException("존재하지 않는 상품입니다. ${itemId}")

        // 배송정보 생성
        val delivery = Delivery().apply {
            address = member.address
        }

        // 주문상품 생성
        val orderItem = OrderItem.createOrderItem(
            item = item,
            orderPrice = item.price,
            count = count
        )

        // 주문 생성
        val order = Order.createOrder(member = member, delivery = delivery, orderItem)

        // 주문 저장
        // Cascade ALL option 덕분에 여기서만 save 해주면 관련 엔티티 내용 전부 자동 저장
        orderRepository.save(order)

        return order.id
    }

    /**
     * 취소
     */
    @Transactional
    fun cancelOrder(orderId: Long) {
        // 주문 엔티티 조회
        val order = orderRepository.findOne(orderId)
            ?: throw NotFoundException("존재하지 않는 주문입니다. ${orderId}")

        // 주문 취소
        order.cancel()
    }

    /**
     * 검색
     */
    fun findOrders(orderSearch: OrderSearch): List<Order> {
        return orderRepository.findAllByString(orderSearch)
    }
}